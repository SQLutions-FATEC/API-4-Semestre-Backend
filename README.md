# Backend

# Tecnologias usadas

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

---

## 🧱 Estrutura do Projeto

```text
API-4-Semestre-Backend/          
├── .gitignore                  
├── README.md
├── pom.xml                      
├── deploy/
│   ├── backend/    
│   ├── data/     
│   ├── frontend/     
│   ├── importer/  
│   ├── postgres/  
│   │    └── init.sql
│   ├── importer/
│   ├── docker-compose.yaml     
│   └── .env.example                 
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── sqlutions/
│       │           └── altave/
│       │               ├── Api4SemestreBackendApplication.java
│       │               ├── config/       
│       │               ├── controller/   
│       │               ├── dto/          
│       │               ├── entity/       
│       │               ├── exception/    
│       │               ├── repository/  
│       │               └── service/      
│       │                     
│       └── resources/
│           └── application.properties    
│               
└── target/                               
```

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas:

-   [Visual Studio Code](https://code.visualstudio.com/)
-   [Extension Pack for Java (VS Code)](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
-   [Java 17](https://www.azul.com/downloads/?version=java-17-lts) (a versão definida no `pom.xml` é a 17)
-   Docker
-   Git

---

## ⚙️ Guia de Instalação e Execução

Siga os passos abaixo para configurar e executar o projeto localmente.

### 1. Clone o Repositório

```bash
git clone https://github.com/SQLutions-FATEC/API-4-Semestre-Backend.git
cd API-4-Semestre-Backend
```

### 2. Execute o Banco de Dados com Docker

Este projeto utiliza um banco de dados PostgreSQL gerenciado pelo Docker. Na raiz do projeto, execute o comando:

```bash
docker compose up -d
```

Isso irá:
-   Baixar a imagem do PostgreSQL, se necessário.
-   Criar e iniciar um container para o banco de dados.
-   Expor a porta `5432` para a sua máquina local.
-   **Inicializar o banco de dados:** O script `deploy/init.sql` será executado automaticamente para criar as tabelas e tipos necessários.

Para verificar se o container está em execução, utilize:
```bash
docker ps
```

### 3. Configure o Ambiente no VS Code

1.  Abra a pasta do projeto (`API-4-Semestre-Backend`) no Visual Studio Code.
2.  Aguarde o VS Code e as extensões de Java carregarem o projeto.
3.  Certifique-se de que o VS Code está utilizando a JDK **Java 17**. Normalmente, isso é detectado automaticamente. Caso contrário, você pode configurar manualmente pressionando `Ctrl+Shift+P` e procurando por `Java: Configure Java Runtime`.

### 4. Execute o Backend

1.  Navegue até o arquivo da classe principal: `src/main/java/com/sqlutions/altave/Api4SemestreBackendApplication.java`.
2.  Clique no botão `Run` que aparece acima do método `main` ou pressione `F5` para iniciar o projeto.

A API estará disponível em `http://localhost:8080`.

---

## 📌 Padrão de Commits e Branches

Todos os commits devem ser escritos em inglês e seguir o formato:

`<tipo>(SCRUM-<número>): descrição em inglês`

**Exemplos:**
-   `feat(SCRUM-68): implement DTO standardization with english attributes`
-   `fix(SCRUM-75): correct null pointer exception in login service`
-   `docs(SCRUM-12): update installation instructions in README`

As branches devem usar o padrão:

`SCRUM-<número>/descrição-em-inglês`

**Exemplo:**
`SCRUM-81/add-table-to-view-employees`