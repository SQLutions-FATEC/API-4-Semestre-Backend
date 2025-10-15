import psycopg2
import os

# --- 1. Configurações de Conexão (Variáveis de Ambiente) ---
# O dicionário de conexão é criado usando os.getenv.
CONN_PARAMS = {
    "dbname":    os.getenv("POSTGRES_DB"),
    "user":      os.getenv("POSTGRES_USER"),
    "password": os.getenv("POSTGRES_PASSWORD"),
    # Define um fallback para host e porta, caso não estejam definidos
    "host":      os.getenv("DB_HOST", "custom-postgres"),
    "port":      os.getenv("DB_PORT", "5432")
}

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

def executar_update_endereco_silencioso():
    """
    Estabelece a conexão com o PostgreSQL, executa o comando UPDATE de 
    geoprocessamento e fecha a conexão, sem gerar saída no console.
    Em caso de erro, a transação é revertida (rollback).
    """
    conn = None
    
    # Verifica se as variáveis essenciais de DB foram carregadas
    if not all(CONN_PARAMS.values()):
        # Esta linha não será mostrada no console, mas é útil se o código for depurado
        # em um ambiente que capture exceções.
        raise ValueError("Variáveis de ambiente do PostgreSQL ausentes. Não é possível conectar.")
    
    try:
        # 2. Estabelece a conexão usando o dicionário de parâmetros
        conn = psycopg2.connect(**CONN_PARAMS)
        
        cur = conn.cursor()
        
        # 3. Executa o comando SQL
        cur.execute(SQL_UPDATE_COMMAND)
        
        # 4. Confirma as alterações (COMMIT)
        conn.commit()
        
        # 5. Fecha o cursor
        cur.close()

    except Exception:
        # Se ocorrer qualquer erro, tenta reverter a transação.
        if conn:
            conn.rollback()
        # O erro é capturado e o programa termina silenciosamente (sem raise).
        
    finally:
        # 6. Fecha a conexão, se ela foi estabelecida
        if conn:
            conn.close()

if __name__ == "__main__":
    executar_update_endereco_silencioso()
