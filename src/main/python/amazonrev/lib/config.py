import os

from dotenv import load_dotenv


load_dotenv(dotenv_path='env')

PYTHON_ENV = os.environ.get('PYTHON_ENV', 'production')
DEV = PYTHON_ENV == 'development'
MODEL_DIR = os.environ.get('MODEL_DIR', './model/')

print(f'PYTHON_ENV = {PYTHON_ENV}')
print(f'MODEL_DIR = {MODEL_DIR}')
