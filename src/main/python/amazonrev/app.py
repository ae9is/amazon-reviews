import sys

import fastapi as fast
from pydantic import BaseModel

from lib.response import generate_response as r
from lib.logger import debug
from lib.tensor import tensor_to_string
from embeddings import generate_embeddings


app = fast.FastAPI()


@app.get('/v1/healthz', tags=['healthz'])
def healthz():
  return r('OK')


@app.get('/v1/torch/version', tags=['torch'])
async def torch_version():
  debug(f'Using python {sys.version}')
  debug('Trying to load torch...')
  import torch

  version = torch.__version__
  cuda = {
    'version': torch.version.cuda,
    'available': torch.cuda.is_available(),
  }
  body = {
    '__version__': version,
    'cuda': cuda,
  }
  return r(body)


class Embeddable(BaseModel):
  text: str

@app.post('/v1/embedding/create', tags=['embedding'])
async def embedding_create(embeddable: Embeddable):
  embeddings = generate_embeddings([embeddable.text])
  if embeddings is None or len(embeddings) < 1:
    return r(body='Error generating embedding', status=500)
  body = {
    'data': tensor_to_string(embeddings[0]),
  }
  return r(body)


if __name__ == '__main__':
  import uvicorn

  uvicorn.run(app, host='0.0.0.0', port=5000)
