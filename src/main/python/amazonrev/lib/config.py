import os

from dotenv import load_dotenv  # , dotenv_values

load_dotenv(dotenv_path='env')
# print(dotenv_values(dotenv_path='env'))

PYTHON_ENV = os.environ.get('PYTHON_ENV', 'production')
DEV = PYTHON_ENV == 'development'

MODEL_DIR = os.environ.get('MODEL_DIR', './data/models/blair-roberta-base/')
