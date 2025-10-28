import psycopg2
import time
import os
import glob
from concurrent.futures import ProcessPoolExecutor, as_completed

# --- DATABASE CONNECTION DETAILS (from environment variables) ---
# This dictionary is now a global template for connections.
CONN_PARAMS = {
    "dbname":   os.getenv("POSTGRES_DB"),
    "user":     os.getenv("POSTGRES_USER"),
    "password": os.getenv("POSTGRES_PASSWORD"),
    "host":     os.getenv("DB_HOST", "custom-postgres"),
    "port":     os.getenv("DB_PORT", "5432")
}

# --- Find all CSV files in the data directory ---
CSV_FILE_PATH = '/app/data/'
ARQUIVOS = glob.glob(os.path.join(CSV_FILE_PATH, '*.csv'))

def process_file(file_path):
    """
    Worker function to process a single CSV file.
    Each worker process creates its own database connection.
    """
    conn = None
    try:
        conn = psycopg2.connect(**CONN_PARAMS)
        cur = conn.cursor()
        
        file_name = os.path.basename(file_path)
        print(f"🔄 Iniciando carga do arquivo '{file_name}'...")
        
        with open(file_path, 'r', encoding='utf-8') as f:
            next(f)  # Skip header
            # Use copy_expert for efficient bulk loading from STDIN
            cur.copy_expert(sql="COPY public.staging_leitura FROM STDIN WITH CSV", file=f)
        
        conn.commit()
        print(f"✅ Arquivo '{file_name}' carregado com sucesso.")
        return file_name, None
    except Exception as e:
        if conn:
            conn.rollback()
        print(f"❌ Erro ao processar '{os.path.basename(file_path)}': {e}")
        return os.path.basename(file_path), str(e)
    finally:
        if conn:
            conn.close()

def check_for_data():
    import time
    retries = 5
    while retries > 0:
        try:
            conn = psycopg2.connect(**CONN_PARAMS)
            break
        except psycopg2.OperationalError:
            retries -= 1
            print("⏳ Banco de dados indisponível, tentando novamente...")
            time.sleep(5)
    else:
        print("❌ Não foi possível conectar ao banco de dados após várias tentativas.")
        return False

    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM leitura;")
    count = cursor.fetchone()[0]
    cursor.close()
    conn.close()

    if count > 0:
        print("Dados encontrados no banco. Pulando processo de importação.")
        return True
    else:
        print("Banco de dados vazio. Iniciando processo de importação...")
        return False

def main_etl_process():
    """
    Main function orchestrating the entire ETL process.
    """
    conn = None
    # Use a main connection for setup, transformation, and cleanup.
    retries = 5
    while retries > 0:
        try:
            conn = psycopg2.connect(**CONN_PARAMS)
            print("✅ Conectado ao banco de dados principal com sucesso!")
            break
        except psycopg2.OperationalError:
            retries -= 1
            print("⏳ Banco de dados indisponível, tentando novamente...")
            time.sleep(5)
    
    if not conn:
        print("❌ Não foi possível conectar ao banco de dados após várias tentativas.")
        return

    try:
        cur = conn.cursor()

        # --- Etapa 0: Setup ---
        print("\n--- Etapa 0: Configuração ---")
        # Use UNLOGGED TABLE for maximum staging performance
        cur.execute("""
            CREATE UNLOGGED TABLE IF NOT EXISTS public.staging_leitura (
                camera_latitude DECIMAL(9,6),
                camera_longitude DECIMAL(9,6),
                camera_numero VARCHAR(9),
                DataHoraTz TIMESTAMP,
                endereco VARCHAR(150),
                tipoVeiculo VARCHAR(50),
                velocidade INT,
                velocidadeRegulamentada INT
            );
        """)
        cur.execute("TRUNCATE TABLE public.staging_leitura;")
        conn.commit()
        print("✅ Tabela staging (UNLOGGED) pronta.")

        # --- Etapa 1: Parallel Bulk Load ---
        print("\n--- Etapa 1: Carga Paralela dos Arquivos CSV ---")
        start_time_load = time.time()
        # Use as many workers as there are files, or os.cpu_count()
        max_workers = min(len(ARQUIVOS), os.cpu_count() or 4)
        print(f"🚀 Iniciando pool com {max_workers} processos...")
        
        with ProcessPoolExecutor(max_workers=max_workers) as executor:
            futures = [executor.submit(process_file, file_path) for file_path in ARQUIVOS]
            
            for future in as_completed(futures):
                file_name, error = future.result()
                if error:
                    print(f"‼️ Falha no processamento do arquivo: {file_name}")
        
        print(f"✅ Carga paralela concluída em {time.time() - start_time_load:.2f} segundos.")

        # --- Etapa 2: Synchronized Data Transformation ---
        print("\n--- Etapa 2: Movendo dados para as Tabelas Finais (Método Otimizado) ---")
        start_time_move = time.time()

        # Temporarily disable triggers (including FK constraints) for performance
        cur.execute("SET session_replication_role = 'replica';")
        
        cur.execute("""
            CREATE TEMP TABLE tmp_distinct_radars AS
            SELECT DISTINCT ON (camera_numero)
                camera_numero, endereco, camera_latitude,
                camera_longitude, velocidadeRegulamentada
            FROM staging_leitura;
        """)
        
        cur.execute("""
            INSERT INTO endereco (ende)
            SELECT DISTINCT endereco FROM tmp_distinct_radars
            ON CONFLICT (ende) DO NOTHING;
        """)

        cur.execute("""
            INSERT INTO radar (id, id_end, localizacao, vel_reg)
            SELECT
                tmp.camera_numero, 
                e.id, 
                ST_SetSRID(ST_MakePoint(tmp.camera_longitude, tmp.camera_latitude), 4326),
                tmp.velocidadeRegulamentada
            FROM tmp_distinct_radars tmp
            JOIN endereco e ON tmp.endereco = e.ende
            ON CONFLICT (id) DO NOTHING;
        """)
        
        cur.execute("""
            INSERT INTO leitura (id_rad, dat_hora, tip_vei, vel)
            SELECT camera_numero, DataHoraTz, tipoVeiculo::tipo_veiculo, velocidade
            FROM staging_leitura;
        """)

        # IMPORTANT: Re-enable triggers before committing
        cur.execute("SET session_replication_role = 'origin';")
        conn.commit()
        print(f"✅ Dados movidos com sucesso em {time.time() - start_time_move:.2f} segundos.")

        # --- Etapa 2d: Database Optimization ---
        print("\n--- Etapa 2d: Otimização do Banco de Dados ---")
        start_time_index = time.time()
        print("⏳ Criando índice na coluna 'dat_hora' para acelerar consultas...")
        cur.execute("CREATE INDEX IF NOT EXISTS idx_leitura_dat_hora ON leitura (dat_hora);")
        conn.commit()
        print(f"✅ Índice 'idx_leitura_dat_hora' criado com sucesso em {time.time() - start_time_index:.2f} segundos.")

        # --- Etapa 3: Cleanup ---
        print("\n--- Etapa 3: Limpeza ---")
        cur.execute("DROP TABLE public.staging_leitura;")
        conn.commit()
        print("✅ Tabela staging removida.")

    except psycopg2.Error as e:
        print(f"❌ Erro no banco de dados: {e}")
        if conn: conn.rollback()
    except Exception as e:
        print(f"❌ Um erro inesperado ocorreu: {e}")
    finally:
        # Ensure the main connection is always closed.
        if conn:
            # As a safeguard, ensure replication role is reset if an error occurred mid-transaction.
            try:
                cur.execute("SET session_replication_role = 'origin';")
                conn.commit()
            except psycopg2.Error:
                pass  # Connection might be closed already
            conn.close()
        print("\nConexão principal com o banco de dados encerrada.")

if __name__ == "__main__":
    data_found = check_for_data()
    if not data_found:
        total_start_time = time.time()
        main_etl_process()
        print(f"\n✨ Processo completo finalizado em {time.time() - total_start_time:.2f} segundos. ✨")
