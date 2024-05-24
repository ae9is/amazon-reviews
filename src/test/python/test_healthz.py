import requests as req

from src.test.python.config import MODEL_API_URL


def test_healthz(api_url=MODEL_API_URL):
  url = f'{api_url}/v1/healthz'
  response: req.Response = req.request('GET', url)
  assert response.status_code == 200
