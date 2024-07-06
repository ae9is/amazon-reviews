import json

from lib.response import generate_response as r
from lib.tensor import tensor_to_string
from lib.logger import log


def handler(event, context):
  '''
  /v1/embedding/create
  '''
  log('Handle embedding/create')
  from embeddings import generate_embeddings
  try:
    body = json.loads(event['body'])
    text = body['text']
    if text is None or len(text) < 1:
      raise Exception()
  except Exception:
    return r(body='Must provide text to embed', status=400)
  embeddings = generate_embeddings([text])
  if embeddings is None or len(embeddings) < 1:
    return r(body='Error generating embedding', status=500)
  body = {
    'data': tensor_to_string(embeddings[0]),
  }
  return r(body)
