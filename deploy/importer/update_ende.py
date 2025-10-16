import psycopg2
from psycopg2 import sql
import os
from typing import Dict, Any

# --- 1. Configurações de Conexão (Variáveis de Ambiente) ---
# O dicionário de conexão é criado usando os.getenv.
CONN_PARAMS = {
    "dbname":     os.getenv("POSTGRES_DB"),
    "user":       os.getenv("POSTGRES_USER"),
    "password":   os.getenv("POSTGRES_PASSWORD"),
    # Define um fallback para host e porta, caso não estejam definidos
    "host":       os.getenv("DB_HOST", "custom-postgres"),
    "port":       os.getenv("DB_PORT", "5432")
}

# --- 2. Configurações da Coluna de Destino ---
NOME_TABELA = "endereco"
COLUNA_ALVO = "regiao" # Coluna que será preenchida pelo geoprocessamento

# O comando SQL que você deseja executar
SQL_UPDATE_COMMAND = """
UPDATE endereco AS e
SET regiao = r.nome_regiao
FROM radar AS ra
JOIN regioes AS r ON ST_Contains(
    r.area_regiao, 
    ra.localizacao 
)
WHERE e.id = ra.id_end;
"""

def check_if_data_exists_in_column(conn_params: Dict[str, Any], table_name: str, column_name: str) -> bool:
    """
    Verifica se a coluna de destino contém algum registro NÃO NULO.
    Retorna True se houver dados preenchidos, False caso contrário ou em caso de erro.
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
        
        # Query para contar o número de linhas onde a coluna ALVO não é nula
        count_query = sql.SQL("SELECT COUNT(*) FROM {} WHERE {} IS NOT NULL;").format(
            sql.Identifier(table_name),
            sql.Identifier(column_name)
        )
        
        cur.execute(count_query)
        count = cur.fetchone()[0]
        
        return count > 0
        
    except psycopg2.ProgrammingError as e:
        # Captura erro se a tabela não existir 
        if "does not exist" in str(e) or "column" in str(e) and "does not exist" in str(e):
             print(f"AVISO: Tabela ou coluna ('{column_name}') não encontrada. Assumindo que está vazia/não preenchida.")
             return False
        raise # Levanta outros erros de programação
    except Exception as e:
        print(f"❌ ERRO inesperado durante a checagem da tabela '{table_name}': {e}")
        return False
    finally:
        if cur: cur.close()
        if conn: conn.close()


def executar_update_endereco_silencioso():
    """
    Estabelece a conexão com o PostgreSQL, executa o comando UPDATE de 
    geoprocessamento e fecha a conexão, sem gerar saída no console.
    Em caso de erro, a transação é revertida (rollback).
    """
    conn = None
    
    # Verifica se as variáveis essenciais de DB foram carregadas
    if not all(CONN_PARAMS.values()):
        raise ValueError("Variáveis de ambiente do PostgreSQL ausentes. Não é possível conectar.")
    
    try:
        # 3. Estabelece a conexão usando o dicionário de parâmetros
        conn = psycopg2.connect(**CONN_PARAMS)
        
        cur = conn.cursor()
        
        # 4. Executa o comando SQL
        cur.execute(SQL_UPDATE_COMMAND)
        
        # 5. Confirma as alterações (COMMIT)
        conn.commit()
        
        # 6. Fecha o cursor
        cur.close()

    except Exception as e:
        print(f"❌ ERRO durante a execução do Geoprocessamento: {e}")
        # Se ocorrer qualquer erro, tenta reverter a transação.
        if conn:
            conn.rollback()
        # Não levanta o erro (mantendo o comportamento 'silencioso')
        
    finally:
        # 7. Fecha a conexão, se ela foi estabelecida
        if conn:
            conn.close()

if __name__ == "__main__":
    
    # NOVO PASSO: Checagem antes de executar a lógica principal
    if check_if_data_exists_in_column(CONN_PARAMS, NOME_TABELA, COLUNA_ALVO):
        print(f"\n=======================================================")
        print(f"✅ COLUNA '{COLUNA_ALVO}' JÁ PREENCHIDA.")
        print(f"O Geoprocessamento (Update) será IGNORADO conforme solicitado.")
        print(f"=======================================================\n")
    else:
        print(f"\n=======================================================")
        print(f"Coluna '{COLUNA_ALVO}' VAZIA. Iniciando o Geoprocessamento...")
        print(f"=======================================================\n")
        
        try:
            executar_update_endereco_silencioso()
            print("Processo de Geoprocessamento concluído.")
        except ValueError as e:
            print(f"❌ ERRO FATAL: {e}")