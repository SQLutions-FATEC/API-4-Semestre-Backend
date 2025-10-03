#!/bin/bash

set -e

python import_data.py

if [ $? -eq 0 ] then
  echo ">>> [IMPORTER] Data import successful. Reconfiguring PostgreSQL for production..."

  # Use psql to connect and change settings.
  # These settings will be written to postgresql.auto.conf and persist in the volume.
  psql "postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@${DB_HOST}:5432/${POSTGRES_DB}" <<-EOSQL
    -- Set fsync back to on for data safety
    ALTER SYSTEM SET fsync = on;
    -- Adjust other parameters for normal operation
    ALTER SYSTEM SET maintenance_work_mem = '256MB';
    ALTER SYSTEM SET max_wal_size = '1GB';
    ALTER SYSTEM SET checkpoint_timeout = '300s';
    ALTER SYSTEM SET shared_buffers = '1GB';
    -- Apply the changes by reloading the configuration
    SELECT pg_reload_conf();
EOSQL

  echo ">>> [IMPORTER] PostgreSQL reconfiguration complete. Importer finished."
else
  echo ">>> [IMPORTER] Data import failed. Aborting."
  exit 1
fi
