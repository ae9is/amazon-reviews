import os

from dotenv import load_dotenv


load_dotenv(dotenv_path='env')

PYTHON_ENV = os.environ.get('PYTHON_ENV', 'production')
DEV = PYTHON_ENV == 'development'
MODEL_DIR = os.environ.get('MODEL_DIR', './model/')

print(f'PYTHON_ENV = {PYTHON_ENV}')
print(f'MODEL_DIR = {MODEL_DIR}')

ITEMS_METADATA_FILE = os.environ.get('ITEMS_METADATA_FILE', 'data/import/meta_Musical_Instruments.jsonl')
EMBEDDINGS_FILE = os.environ.get('EMBEDDINGS_FILE', 'data/import/item_embed.csv')
COMPLETED_INDICES_FILE = os.environ.get('COMPLETED_INDICES_FILE', 'data/import/item_embed_completed_indices.txt')
