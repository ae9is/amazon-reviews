import requests as req
import json
import torch

from src.test.python.config import MODEL_API_URL


def json_equals(a, b) -> bool:
  a_str = json.dumps(a, sort_keys=True)
  b_str = json.dumps(b, sort_keys=True)
  return a_str == b_str


def tensor_string_to_list(tensor_string: str) -> list[float]:
  """
  Expects a tensor string in the format '[1.0,0.0003,5]' and 
  returns the tensor as an array of floats like [1.0, 0.0003, 5].
  """
  return list(map(float, tensor_string.strip('[]').split(',')))  


def tensor_string_to_tensor(tensor_string: str) -> torch.Tensor:
  tensor_list = tensor_string_to_list(tensor_string)
  return torch.tensor(tensor_list, dtype=torch.float32)


def test_embeddings(api_url=MODEL_API_URL):
  # Expected size of the embedding
  # ref: data/models/blair-roberta-base/config.json
  MODEL_HIDDEN_SIZE = 768
  # How close the expected and calculated embedding vectors should be to pass test, inner product
  # ref: https://huggingface.co/hyp1231/blair-roberta-base#use-with-huggingface
  THRESHOLD_DISTANCE = 0.95
  url = f'{api_url}/v1/embedding/create'
  data = {
    "text": "Something to be encoded"
  }
  expected_response_file = 'src/test/resources/python/embedding_create.json'
  response: req.Response = req.request('POST', url, data=json.dumps(data))
  assert response.status_code == 200
  resp_json = response.json()
  print(f'Response data: {json.dumps(resp_json)[:60]}...')
  resp_embedding = tensor_string_to_tensor(resp_json['data'])
  print(f'Response embedding length: {resp_embedding.size(dim=0)}')
  assert resp_embedding.size(dim=0) == MODEL_HIDDEN_SIZE
  with open(expected_response_file) as f:
    expected_json = json.load(f)
    expected_embedding = tensor_string_to_tensor(expected_json['data'])
    distance = resp_embedding @ expected_embedding
    print(f'Distance: {distance}')
    assert distance >= THRESHOLD_DISTANCE
    # Note: Even tiny changes in model output from normal variation will break exact equality
    #assert json_equals(resp_json, expected_json)
