FROM postgis/postgis:16-3.4

ARG POSTGRES_USER
ARG POSTGRES_PASSWORD
ARG POSTGRES_DB

COPY init.sql /docker-entrypoint-initdb.d/
COPY seed_usuarios_iniciais.sql /docker-entrypoint-initdb.d/
COPY entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["postgres"]
