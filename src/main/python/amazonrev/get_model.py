from huggingface_hub import snapshot_download

from lib.config import MODEL_DIR, MODEL_NAME
from lib.logger import log


if __name__ == '__main__':
  log(f'Downloading model {MODEL_NAME} ...')
  snapshot_download(repo_id=MODEL_NAME, allow_patterns=['*.json', '*.safetensors', '*.txt'], local_dir=MODEL_DIR)
  log('Done')
