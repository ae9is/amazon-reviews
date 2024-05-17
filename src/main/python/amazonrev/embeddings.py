import datetime as dt

import torch
from transformers import AutoModel, AutoTokenizer, RobertaModel, RobertaTokenizerFast

from lib.config import MODEL_DIR
from lib.logger import log


log(f'Loading tokenizer and model at {MODEL_DIR} ...')
tokenizer: RobertaTokenizerFast = AutoTokenizer.from_pretrained(MODEL_DIR)
model: RobertaModel = AutoModel.from_pretrained(MODEL_DIR)
device = 'cuda:0' if torch.cuda.is_available() else 'cpu'
if device == 'cuda:0':
  log(f'Offloading model and tokenizer to {device} ...')
  model = model.to(device)


def generate_embeddings(texts: list[str]) -> list[float]:
  embeddings: torch.Tensor = None
  start = dt.datetime.now()
  log(f'Generating embeddings @ {start } ...')
  inputs = tokenizer(texts, padding=True, truncation=True, max_length=512, return_tensors="pt").to(device=device)
  with torch.no_grad():
    embeddings = model(**inputs, return_dict=True).last_hidden_state[:, 0]  # Crashes silently here if too many inputs
    embeddings = embeddings / embeddings.norm(dim=1, keepdim=True)
  end = dt.datetime.now()
  log(f'Generation took: {end - start}')
  return embeddings
