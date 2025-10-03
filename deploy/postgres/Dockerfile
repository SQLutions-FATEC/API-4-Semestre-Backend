FROM postgis:16

ENV POSTGRES_USER sqlutions
ENV POSTGRES_PASSWORD api
ENV POSTGRES_DB api

COPY init.sql /docker-entrypoint-initdb.d/
COPY seed_usuarios_iniciais.sql /docker-entrypoint-initdb/
