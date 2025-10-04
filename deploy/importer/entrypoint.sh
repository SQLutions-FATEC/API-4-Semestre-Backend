#!/bin/bash
# We remove 'set -e' to ensure reconfiguration runs even if the python script exits with a non-zero code after skipping the import.
# The python script should still handle its own critical errors and exit if the import itself fails.

echo ">>> [IMPORTER] Running data import check/process..."

# Run the Python script to import data if necessary.
python import_data.py

echo ">>> [IMPORTER] Import process finished. Applying production settings to PostgreSQL..."

# ALWAYS run the reconfiguration to ensure the database is in a safe state.
# This command is idempotent; running it multiple times has no negative effect.
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
