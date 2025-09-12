FROM postgres:16

ENV POSTGRES_USER sqlutions
ENV POSTGRES_PASSWORD api
ENV POSTGRES_DB api

USER postgres

COPY init.sql /docker-entrypoint-initdb.d/
