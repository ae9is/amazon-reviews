import os

from dotenv import load_dotenv


load_dotenv(dotenv_path='env')

MODEL_API_URL = os.environ.get('MODEL_API_URL', 'http://localhost:5000')

print(f'MODEL_API_URL = {MODEL_API_URL}')
