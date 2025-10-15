import pandas as pd
import psycopg2
from psycopg2 import sql
from typing import Dict, Any
import os # <-- Adicionado para usar variáveis de ambiente

# --- Configurações do Banco de Dados ---
# ATENÇÃO: Substituído DB_CONFIG pelo seu novo dicionário de conexão lendo do ambiente
CONN_PARAMS = {
    "dbname":   os.getenv("POSTGRES_DB"),
    "user":     os.getenv("POSTGRES_USER"),
    "password": os.getenv("POSTGRES_PASSWORD"),
    "host":     os.getenv("DB_HOST", "custom-postgres"),
    "port":     os.getenv("DB_PORT", "5432")
}

# Configuração do nome do arquivo CSV
CSV_FILE = "dados_com_linhas.csv" 

def process_data_and_import(conn_params: Dict[str, Any]):
    """
    Lê o CSV e faz o UPDATE/INSERT na tabela 'endereco' usando o id_endereco 
    e a linestring do próprio CSV.
    """
    conn = None
    cur = None
    df_to_process = pd.DataFrame()

    try:
        # 1. Lendo arquivo CSV
        df = pd.read_csv(CSV_FILE, sep=',', header=0, skipinitialspace=True)
        
        # GARANTIA: Converter id_endereco para inteiro (pode vir como float/nan se houver nulos)
        if 'id_endereco' in df.columns:
            # Preenche N/A com um valor que será tratado como não-ID (ex: 0) e converte para int
            df['id_endereco'] = df['id_endereco'].fillna(0).astype(int) 
        else:
            # Caso a coluna não exista, cria-a com 0 para forçar o INSERT
            print("AVISO: Coluna 'id_endereco' não encontrada no CSV. Assumindo que você quer Inserir novos registros.")
            df['id_endereco'] = 0 
            
        # Verifica se a coluna 'linestring' existe e é a que contém o WKT
        if 'linestring' not in df.columns:
            print("ERRO: Coluna 'linestring' (WKT) não encontrada no CSV.")
            return

    except FileNotFoundError:
        print(f"ERRO: Arquivo '{CSV_FILE}' não encontrado.")
        return
    except Exception as e:
        print(f"ERRO ao ler o CSV: {e}")
        return
    
    df_to_process = df.copy() 
    
    # --- Conexão com o Banco de Dados ---
    try:
        # CONEXÃO AGORA USA conn_params
        conn = psycopg2.connect(**conn_params)
        cur = conn.cursor()
        
        rows_updated = 0
        rows_inserted = 0
        
        # --- Processamento e UPDATE/INSERT ---
        for index, row in df_to_process.iterrows():
            
            id_endereco = row.get('id_endereco', 0)
            nome_endereco = row['nome_endereco']
            wkt_linestring = row['linestring']
            
            # Pula linhas onde a linestring possa estar nula/vazia (se houver)
            if pd.isna(wkt_linestring) or not wkt_linestring.strip():
                continue # Silenciosamente ignora a linha

            # --- Execução no BD ---
            
            if id_endereco > 0:
                # LÓGICA DE ATUALIZAÇÃO (UPDATE)
                update_query = sql.SQL("""
                    UPDATE endereco 
                    SET trecho = ST_GeomFromText(%s, 4326)
                    WHERE id = %s;
                """)
                cur.execute(update_query, (wkt_linestring, id_endereco))
                
                if cur.rowcount > 0:
                    rows_updated += 1
            
            else:
                # LÓGICA DE INSERÇÃO (INSERT)
                insert_query = sql.SQL("""
                    INSERT INTO endereco (ende, trecho) 
                    VALUES (%s, ST_GeomFromText(%s, 4326)) 
                    ON CONFLICT (ende) DO NOTHING;
                """)
                cur.execute(insert_query, (nome_endereco, wkt_linestring))
                
                if cur.rowcount > 0:
                    rows_inserted += 1

        conn.commit()
    
    except psycopg2.Error as e:
        print(f"ERRO de Banco de Dados: {e}")
        if conn:
            conn.rollback()
    except Exception as e:
        print(f"Ocorreu um erro inesperado: {e}")
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

if __name__ == "__main__":
    process_data_and_import(CONN_PARAMS) 