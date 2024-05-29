# data/models

Directory to put downloaded BLaIR model files from Huggingface.

See: 
- [BLaIR-roberta-base](https://huggingface.co/hyp1231/blair-roberta-base)
- [BLaIR-roberta-large](https://huggingface.co/hyp1231/blair-roberta-large)

Set environment variable `MODEL_DIR` appropriately in `.env`.

Note: we bypass the default Hugging Face cache directory (`~/.cache/huggingface/hub`) for ease in bundling the model files into the Python API Docker image.
