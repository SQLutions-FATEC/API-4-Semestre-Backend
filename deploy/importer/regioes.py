import pandas as pd
import psycopg2
from psycopg2 import sql
import os

# --- 1. CONFIGURAÇÕES DE CONEXÃO (Carregadas das Variáveis de Ambiente) ---
# Usamos os.getenv para carregar as credenciais, garantindo segurança.
# Definimos um fallback ("custom-postgres" e "5432") para host e porta.
CONN_PARAMS = {
    "dbname":    os.getenv("POSTGRES_DB"),
    "user":      os.getenv("POSTGRES_USER"),
    "password": os.getenv("POSTGRES_PASSWORD"),
    "host":      os.getenv("DB_HOST", "custom-postgres"),
    "port":      os.getenv("DB_PORT", "5432")
}

# --- 2. CONFIGURAÇÕES GEOESPACIAIS ---
SRID_CORRETO = 4326        # SRID correto para WGS 84 (Latitude/Longitude)
TABELA_DESTINO = 'regioes' # Nome da sua tabela de destino
COLUNA_NOME = 'nome_regiao' # Nome da coluna que armazena o nome da região
COLUNA_GEOM = 'area_regiao' # Nome da coluna GEOMETRY/GEOGRAPHY
COLUNA_LAT = 'Latitude_4326'  # Nome da coluna de Latitude/Y nos arquivos CSV transformados
COLUNA_LONG = 'Longitude_4326' # Nome da coluna de Longitude/X nos arquivos CSV transformados

def processar_csv_e_inserir(file_path):
    """
    Processa um arquivo CSV de polígonos, cria a geometria WKT POLYGON
    e a insere na tabela 'regioes' do PostgreSQL usando as variáveis de ambiente.
    """
    conn = None
    cur = None
    # Extrai o nome da região do nome do arquivo
    nome_regiao = os.path.splitext(os.path.basename(file_path))[0]
    
    # 3. VERIFICAÇÃO DE CREDENCIAIS
    if not all(CONN_PARAMS.values()):
        print(f"❌ ERRO: Variáveis de ambiente do PostgreSQL ausentes para o arquivo '{nome_regiao}'.")
        print("Certifique-se de que POSTGRES_DB, POSTGRES_USER e POSTGRES_PASSWORD estão definidos.")
        return
    
    try:
        # 4. LEITURA E PREPARAÇÃO DOS DADOS
        print(f"Iniciando inserção de: {nome_regiao}")
        
        # Leitura robusta do CSV (separador ';' e decimal '.')
        df = pd.read_csv(file_path, sep=';', decimal='.') 

        # Garante que as colunas de coordenadas são numéricas (float)
        for col in [COLUNA_LAT, COLUNA_LONG]:
            if df[col].dtype == object:
                # Trata a vírgula como separador decimal, se for o caso
                df[col] = df[col].astype(str).str.replace(',', '.', regex=False)
            df[col] = pd.to_numeric(df[col])

        # 5. MONTAGEM DA STRING WKT (POLYGON)
        
        # Cria a lista de coordenadas 'X Y' (Longitude, Latitude)
        coordenadas_xy = (df[COLUNA_LONG].astype(str) + ' ' + df[COLUNA_LAT].astype(str)).tolist()
        
        # Garante que o polígono se fecha (último ponto = primeiro ponto)
        if coordenadas_xy[-1] != coordenadas_xy[0]:
            coordenadas_xy.append(coordenadas_xy[0])

        coords_string = ', '.join(coordenadas_xy)
        wkt_polygon = f"POLYGON(({coords_string}))"

        # 6. CONEXÃO E INSERÇÃO NO BANCO DE DADOS
        
        # Usa o dicionário CONN_PARAMS, que contém as variáveis de ambiente
        conn = psycopg2.connect(**CONN_PARAMS)
        cur = conn.cursor()

        # O comando SQL usa ST_GeomFromText para converter a string WKT em GEOMETRY
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

# --- 7. EXECUÇÃO PRINCIPAL ---

# Adapte este caminho para a pasta onde você salvou seus arquivos CSV transformados
# Para ambientes de contêiner, use o caminho absoluto, ex: /app/geo_data
pasta_com_csvs = os.getenv("GEO_CSV_PATH", "./arquivos_regioes") 

if __name__ == "__main__":
    if not os.path.exists(pasta_com_csvs):
        print(f"Crie a pasta '{pasta_com_csvs}' e coloque seus arquivos CSV (já transformados!) nela, ou ajuste a variável de ambiente GEO_CSV_PATH.")
    else:
        # Filtra apenas arquivos CSV
        arquivos_csv = [os.path.join(pasta_com_csvs, f) 
                        for f in os.listdir(pasta_com_csvs) 
                        if f.endswith('.csv')]
        
        if not arquivos_csv:
            print(f"Nenhum arquivo CSV encontrado em '{pasta_com_csvs}'.")
        
        for arquivo in arquivos_csv:
            processar_csv_e_inserir(arquivo)

        print("\nProcesso de inserção de regiões no banco de dados concluído.")