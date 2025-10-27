#!/bin/bash

echo ">>> [IMPORTER] Running data import check/process..."

python import_data.py
python linestring.py
python pontos_Onibus.py
python regioes.py
python update_ende.py

echo ">>> [IMPORTER] Import process finished."
