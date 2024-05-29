import datetime as dt
import math
import os

import torch
import numpy as np
import pandas as pd

from lib.config import ITEMS_METADATA_FILE, EMBEDDINGS_FILE, COMPLETED_INDICES_FILE
from lib.logger import log
from lib.tensor import tensor_to_string


RECORD_SEP = u'\x1e' # noqa
UNIT_SEP = u'\x1f' # noqa
CHUNK_SIZE = 200


def embedding_to_csv_line(line_num: int, embedding: torch.Tensor) -> str:
  line = f'{line_num}{RECORD_SEP}{UNIT_SEP}{tensor_to_string(embedding)}{UNIT_SEP}'
  return line


def split(dataframe: pd.DataFrame, chunk_size: int = CHUNK_SIZE):
  """
  Split a data frame into chunks of a certain size.
  """
  num_chunks = math.ceil(len(item_titles) / chunk_size)
  chunks: list[pd.DataFrame] = []
  for i in range(num_chunks):
    chunks.append(dataframe[i * chunk_size : (i + 1) * chunk_size])
  return chunks


if __name__ == '__main__':
  log('Item metadata parser for embeddings generation')
  log(f'Starting @ {dt.datetime.now()} ...')
  log(f'Torch version: {torch.__version__}')
  log(f'Cuda version: {torch.version.cuda}')
  log(f'Cuda is available: {torch.cuda.is_available()}')
  from embeddings import generate_embeddings

  log(f'Reading item titles from: {ITEMS_METADATA_FILE} ...')
  item_metadata: pd.DataFrame = pd.read_json(path_or_buf=ITEMS_METADATA_FILE, lines=True)
  item_titles: pd.DataFrame = item_metadata['title']
  should_write_header = not os.path.isfile(EMBEDDINGS_FILE)
  with open(EMBEDDINGS_FILE, 'a') as out:
    if should_write_header:
      header = f'item_id{RECORD_SEP}title\n'
      out.write(header)
    with open(COMPLETED_INDICES_FILE, 'a+') as completed:
      completed.seek(0)
      # Get a list of already processed indices to allow stopping and resuming jobs.
      # (Could also be a single number since currently jobs are sequential.)
      completed_indices: list[int] = []
      for line in completed:
        line_indices = [int(i) for i in str(line).strip().split() if i.isnumeric()]
        completed_indices += line_indices
      # Don't use scientific notation for conversion of numpy floats to strings
      np.set_printoptions(suppress=True, formatter={'float_kind': '{:f}'.format})
      idx = -1
      chunk_list = split(item_titles, CHUNK_SIZE)
      num_chunks = len(chunk_list)
      for chunk in chunk_list:
        idx += 1
        if idx in completed_indices:
          continue
        log(f'Processing chunk {idx + 1} of {num_chunks} @ {dt.datetime.now()} ...')
        titles = chunk.to_list()
        chunk_embeddings = generate_embeddings(titles)
        file_chunk = '\n'.join(embedding_to_csv_line(i, embed) for (i, embed) in enumerate(chunk_embeddings, start=idx*CHUNK_SIZE))
        out.write(file_chunk + '\n')
        out.flush()
        completed.write(f'{idx}\n')
        completed.flush()
  log(f'Embeddings should be written to: {EMBEDDINGS_FILE}')
  log(f'Done @ {dt.datetime.now()}')
