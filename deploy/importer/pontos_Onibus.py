import json
import os
import psycopg2
from psycopg2 import sql
from typing import Dict, Any # Adicionado para tipagem

# --- CONFIGURAÇÃO DO BANCO DE DADOS (AGORA USANDO VARIÁVEIS DE AMBIENTE) ---
# ATENÇÃO: Os valores padrões são definidos apenas para o caso de a variável de ambiente não existir.
CONN_PARAMS = {
    "dbname":   os.getenv("POSTGRES_DB"),
    "user":     os.getenv("POSTGRES_USER"),
    "password": os.getenv("POSTGRES_PASSWORD"),
    "host":     os.getenv("DB_HOST", "custom-postgres"),
    "port":     os.getenv("DB_PORT", "5432")
}

NOME_TABELA = "pontos_onibus"
SRID = 4326  # Padrão WGS 84 (GPS)

# Nome do arquivo onde você salvou a saída do Overpass
NOME_ARQUIVO = 'pontos_onibus.json'

def check_if_data_exists(conn_params: Dict[str, Any], table_name: str) -> bool:
    """
    Verifica se a tabela de destino já contém registros (COUNT(*) > 0).
    Retorna True se houver dados, False caso contrário ou em caso de erro.
    """
    conn = None
    cur = None
    
    # Verifica credenciais
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
        # Captura erro se a tabela não existir (o que significa que está vazia)
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


def extrair_dados_para_db(caminho_arquivo):
    """
    Lê o arquivo JSON do Overpass API e extrai ID, latitude e longitude.
    """
    if not os.path.exists(caminho_arquivo):
        # Mantido o raise para erro fatal de arquivo
        raise FileNotFoundError(f"Erro: O arquivo '{caminho_arquivo}' não foi encontrado.")
    
    dados_para_insercao = []
    
    with open(caminho_arquivo, 'r', encoding='utf-8') as f:
        dados = json.load(f)

    elementos = dados.get("elements", [])
    
    for elemento in elementos:
        if elemento.get("type") == "node":
            node_id = elemento.get("id")
            lat = elemento.get("lat")
            lon = elemento.get("lon")
            
            if node_id and lat is not None and lon is not None:
                dados_para_insercao.append({
                    "id": node_id,
                    "latitude": lat,
                    "longitude": lon
                })
                
    return dados_para_insercao

def inserir_pontos_postgis(conn, nome_tabela, pontos, srid):
    """
    Insere os dados (ID e coordenadas) na tabela PostGIS, 
    criando o objeto POINT.
    """
    
    # 1. Cria ou verifica a tabela
    create_table_query = sql.SQL("""
        CREATE TABLE IF NOT EXISTS {} (
            id BIGINT PRIMARY KEY,
            ponto GEOMETRY(Point, {})
        );
    """).format(sql.Identifier(nome_tabela), sql.Literal(srid))
    
    insert_query = sql.SQL("""
        INSERT INTO {} (id, ponto) 
        VALUES (%s, ST_SetSRID(ST_MakePoint(%s, %s), %s))
        ON CONFLICT (id) DO UPDATE SET
            ponto = EXCLUDED.ponto;
    """).format(sql.Identifier(nome_tabela))
    # Note: O PostGIS utiliza LON, LAT (Longitude, Latitude) na função ST_MakePoint
    
    pontos_inseridos = 0
    
    try:
        with conn.cursor() as cur:
            # Executa a criação/verificação da tabela
            cur.execute(create_table_query)
            
            # Insere os dados
            for ponto in pontos:
                # Valores a serem inseridos: (id, longitude, latitude, srid)
                valores = (
                    ponto["id"], 
                    ponto["longitude"], 
                    ponto["latitude"], 
                    srid
                )
                cur.execute(insert_query, valores)
                pontos_inseridos += 1
                
            conn.commit()
            print(f"✅ Sucesso: {pontos_inseridos} pontos de ônibus inseridos/atualizados na tabela '{nome_tabela}'.")
            
    except Exception as e:
        # Reverter se houver erro
        conn.rollback()
        raise


# --- FLUXO PRINCIPAL ---
if __name__ == "__main__":
    
    conn = None
    try:
        # NOVO PASSO: Checagem de dados antes de processar
        if check_if_data_exists(CONN_PARAMS, NOME_TABELA):
            print(f"\n=======================================================")
            print(f"✅ CONTEÚDO JÁ PRESENTE: A tabela '{NOME_TABELA}' já contém dados.")
            print(f"A importação será IGNORADA conforme solicitado.")
            print(f"=======================================================\n")
            exit(0) # Encerra o script
            
        print(f"\n=======================================================")
        print(f"Tabela '{NOME_TABELA}' VAZIA. Iniciando o processo de importação.")
        print(f"=======================================================\n")
        
        # 1. Extrair os dados do arquivo JSON
        dados_pontos = extrair_dados_para_db(NOME_ARQUIVO)
        
        # 2. Conectar ao banco de dados
        # A conexão agora usa o novo dicionário CONN_PARAMS
        conn = psycopg2.connect(**CONN_PARAMS)
        
        # 3. Criar a tabela e inserir os dados
        inserir_pontos_postgis(conn, NOME_TABELA, dados_pontos, SRID)
        
    except FileNotFoundError as e:
        print(e) # Mantido para indicar erro fatal de arquivo
    except psycopg2.Error as e:
        print(f"Erro de conexão/PostgreSQL: {e}")
    except Exception as e:
        print(f"Um erro ocorreu: {e}")
    finally:
        # 4. Fechar a conexão
        if conn is not None:
            conn.close()