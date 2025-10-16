import pandas as pd
import psycopg2
from psycopg2 import sql
import os
from typing import Dict, Any

# --- 1. CONFIGURAÇÕES DE CONEXÃO (Carregadas das Variáveis de Ambiente) ---
# Usamos os.getenv para carregar as credenciais, garantindo segurança.
# Definimos um fallback ("custom-postgres" e "5432") para host e porta.
CONN_PARAMS = {
    "dbname":     os.getenv("POSTGRES_DB"),
    "user":       os.getenv("POSTGRES_USER"),
    "password":   os.getenv("POSTGRES_PASSWORD"),
    "host":       os.getenv("DB_HOST", "custom-postgres"),
    "port":       os.getenv("DB_PORT", "5432")
}

# --- 2. CONFIGURAÇÕES GEOESPACIAIS ---
SRID_CORRETO = 4326         # SRID correto para WGS 84 (Latitude/Longitude)
TABELA_DESTINO = 'regioes' # Nome da sua tabela de destino
COLUNA_NOME = 'nome_regiao' # Nome da coluna que armazena o nome da região
COLUNA_GEOM = 'area_regiao' # Nome da coluna GEOMETRY/GEOGRAPHY
COLUNA_LAT = 'Latitude_4326'  # Nome da coluna de Latitude/Y nos arquivos CSV transformados
COLUNA_LONG = 'Longitude_4326' # Nome da coluna de Longitude/X nos arquivos CSV transformados

def check_if_data_exists(conn_params: Dict[str, Any], table_name: str) -> bool:
    """
    Verifica se a tabela de destino já contém registros (COUNT(*) > 0).
    Retorna True se houver dados, False caso contrário ou em caso de erro.
    """
    conn = None
    cur = None
    
    # 3. VERIFICAÇÃO DE CREDENCIAIS
    if not all(conn_params.values()):
        print(f"❌ ERRO: Variáveis de ambiente do PostgreSQL ausentes para checagem da tabela '{table_name}'.")
        return False
        
    try:
        conn = psycopg2.connect(**conn_params)
        cur = conn.cursor()
        
        # Query para contar o número de linhas
        count_query = sql.SQL("SELECT COUNT(*) FROM {};").format(
            sql.Identifier(table_name)
        )
        
        cur.execute(count_query)
        count = cur.fetchone()[0]
        
        return count > 0
        
    except psycopg2.ProgrammingError as e:
        # Captura erro se a tabela 'regioes' ainda não existir (o que significa que está vazia)
        if "does not exist" in str(e):
             print(f"AVISO: Tabela '{table_name}' não encontrada. Assumindo que está vazia.")
             return False
        raise # Levanta outros erros de programação
    except Exception as e:
        print(f"❌ ERRO inesperado durante a checagem da tabela '{table_name}': {e}")
        return False
    finally:
        if cur: cur.close()
        if conn: conn.close()


def processar_csv_e_inserir(file_path):
    """
    Processa um arquivo CSV de polígonos, cria a geometria WKT POLYGON
    e a insere na tabela 'regioes' do PostgreSQL usando as variáveis de ambiente.
    """
    conn = None
    cur = None
    # Extrai o nome da região do nome do arquivo
    nome_regiao = os.path.splitext(os.path.basename(file_path))[0]
    
    # 4. VERIFICAÇÃO DE CREDENCIAIS
    if not all(CONN_PARAMS.values()):
        print(f"❌ ERRO: Variáveis de ambiente do PostgreSQL ausentes para o arquivo '{nome_regiao}'.")
        print("Certifique-se de que POSTGRES_DB, POSTGRES_USER e POSTGRES_PASSWORD estão definidos.")
        return
    
    try:
        # 5. LEITURA E PREPARAÇÃO DOS DADOS
        print(f"Iniciando inserção de: {nome_regiao}")
        
        # Leitura robusta do CSV (separador ';' e decimal '.')
        df = pd.read_csv(file_path, sep=';', decimal='.') 

        # Garante que as colunas de coordenadas são numéricas (float)
        for col in [COLUNA_LAT, COLUNA_LONG]:
            if df[col].dtype == object:
                # Trata a vírgula como separador decimal, se for o caso
                df[col] = df[col].astype(str).str.replace(',', '.', regex=False)
            df[col] = pd.to_numeric(df[col])

        # 6. MONTAGEM DA STRING WKT (POLYGON)
        
        # Cria a lista de coordenadas 'X Y' (Longitude, Latitude)
        coordenadas_xy = (df[COLUNA_LONG].astype(str) + ' ' + df[COLUNA_LAT].astype(str)).tolist()
        
        # Garante que o polígono se fecha (último ponto = primeiro ponto)
        if coordenadas_xy[-1] != coordenadas_xy[0]:
            coordenadas_xy.append(coordenadas_xy[0])

        coords_string = ', '.join(coordenadas_xy)
        wkt_polygon = f"POLYGON(({coords_string}))"

        # 7. CONEXÃO E INSERÇÃO NO BANCO DE DADOS
        
        # Usa o dicionário CONN_PARAMS, que contém as variáveis de ambiente
        conn = psycopg2.connect(**CONN_PARAMS)
        cur = conn.cursor()

        # O comando SQL usa ST_GeomFromText para converter a string WKT em GEOMETRY
        # E usa ON CONFLICT para garantir o Upsert
        query = sql.SQL("""
            INSERT INTO {tabela} ({nome_coluna}, {geom_coluna})
            VALUES (%s, ST_GeomFromText(%s, %s))
            ON CONFLICT ({nome_coluna}) DO UPDATE 
            SET {geom_coluna} = EXCLUDED.{geom_coluna}; 
        """).format(
            tabela=sql.Identifier(TABELA_DESTINO),
            nome_coluna=sql.Identifier(COLUNA_NOME),
            geom_coluna=sql.Identifier(COLUNA_GEOM)
        )
        
        # Executa a inserção (ou atualização se o nome_regiao já existir)
        cur.execute(query, (nome_regiao, wkt_polygon, SRID_CORRETO))
        
        conn.commit()
        print(f"✅ Sucesso: Região '{nome_regiao}' inserida/atualizada com {len(df)} pontos. SRID: {SRID_CORRETO}")
        
    except FileNotFoundError:
        print(f"❌ ERRO: O arquivo '{file_path}' não foi encontrado.")
    except Exception as e:
        print(f"❌ ERRO ao processar {nome_regiao}: {e}")
        if conn:
            conn.rollback()
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()

# --- 8. EXECUÇÃO PRINCIPAL ---

# Adapte este caminho para a pasta onde você salvou seus arquivos CSV transformados
pasta_com_csvs = os.getenv("GEO_CSV_PATH", "./arquivos_regioes") 

if __name__ == "__main__":
    
    # NOVO PASSO: Checagem de dados antes de processar
    if check_if_data_exists(CONN_PARAMS, TABELA_DESTINO):
        print(f"\n=======================================================")
        print(f"✅ CONTEÚDO JÁ PRESENTE: A tabela '{TABELA_DESTINO}' já contém dados.")
        print(f"A importação de novos arquivos CSV será IGNORADA conforme solicitado.")
        print(f"=======================================================\n")
    
    elif not os.path.exists(pasta_com_csvs):
        print(f"Crie a pasta '{pasta_com_csvs}' e coloque seus arquivos CSV (já transformados!) nela, ou ajuste a variável de ambiente GEO_CSV_PATH.")
    
    else:
        print(f"\n=======================================================")
        print(f"Tabela '{TABELA_DESTINO}' VAZIA. Iniciando o processo de importação.")
        print(f"=======================================================\n")
        
        # Filtra apenas arquivos CSV
        arquivos_csv = [os.path.join(pasta_com_csvs, f) 
                        for f in os.listdir(pasta_com_csvs) 
                        if f.endswith('.csv')]
        
        if not arquivos_csv:
            print(f"Nenhum arquivo CSV encontrado em '{pasta_com_csvs}'.")
        
        for arquivo in arquivos_csv:
            processar_csv_e_inserir(arquivo)

        print("\nProcesso de inserção de regiões no banco de dados concluído.")