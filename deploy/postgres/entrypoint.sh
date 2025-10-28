#!/bin/bash
set -e

# Check if we're in import mode
if [ "${POSTGRES_IMPORT_MODE}" = "true" ]; then
    echo "Starting PostgreSQL in IMPORT mode with optimized settings..."
    exec docker-entrypoint.sh postgres \
        -c fsync=off \
        -c maintenance_work_mem=1GB \
        -c max_wal_size=6GB \
        -c checkpoint_timeout=3600s \
        -c checkpoint_completion_target=0.9 \
        -c shared_buffers=2GB \
        -c wal_buffers=16MB
else
    echo "Starting PostgreSQL in PRODUCTION mode with safe settings..."
    exec docker-entrypoint.sh postgres \
        -c fsync=on \
        -c shared_buffers=512MB \
        -c maintenance_work_mem=256MB \
        -c max_wal_size=2GB \
        -c checkpoint_timeout=900s \
        -c checkpoint_completion_target=0.7 \
        -c wal_buffers=8MB
fi
