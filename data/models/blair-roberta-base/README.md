---
license: mit
datasets:
- McAuley-Lab/Amazon-Reviews-2023
language:
- en
tags:
- recommendation
- information retrieval
- Amazon Reviews 2023
base_model: FacebookAI/roberta-base
---

# BLaIR-roberta-base

<!-- Provide a quick summary of what the model is/does. -->

BLaIR, which is short for "**B**ridging **La**nguage and **I**tems for **R**etrieval and **R**ecommendation", is a series of language models pre-trained on Amazon Reviews 2023 dataset.

BLaIR is grounded on pairs of *(item metadata, language context)*, enabling the models to:
* derive strong item text representations, for both recommendation and retrieval;
* predict the most relevant item given simple / complex language context.

[[📑 Paper](https://arxiv.org/abs/2403.03952)] · [[💻 Code](https://github.com/hyp1231/AmazonReviews2023)] · [[🌐 Amazon Reviews 2023 Dataset](https://amazon-reviews-2023.github.io/)] · [[🤗 Huggingface Datasets](https://huggingface.co/datasets/McAuley-Lab/Amazon-Reviews-2023)] · [[🔬 McAuley Lab](https://cseweb.ucsd.edu/~jmcauley/)]

## Model Details

- **Language(s) (NLP):** English
- **License:** MIT
- **Finetuned from model:** [roberta-base](https://huggingface.co/FacebookAI/roberta-base)
- **Repository:** [https://github.com/hyp1231/AmazonReviews2023](https://github.com/hyp1231/AmazonReviews2023)
- **Paper:** [https://arxiv.org/abs/2403.03952](https://arxiv.org/abs/2403.03952)

## Use with HuggingFace

```python
import torch
from transformers import AutoModel, AutoTokenizer


tokenizer = AutoTokenizer.from_pretrained("hyp1231/blair-roberta-base")
model = AutoModel.from_pretrained("hyp1231/blair-roberta-base")

language_context = 'I need a product that can scoop, measure, and rinse grains without the need for multiple utensils and dishes. It would be great if the product has measurements inside and the ability to rinse and drain all in one. I just have to be careful not to pour too much accidentally.'
item_metadata = [
  'Talisman Designs 2-in-1 Measure Rinse & Strain | Holds up to 2 Cups | Food Strainer | Fruit Washing Basket | Strainer & Colander for Kitchen Sink | Dishwasher Safe - Dark Blue. The Measure Rinse & Strain by Talisman Designs is a 2-in-1 kitchen colander and strainer that will measure and rinse up to two cups. Great for any type of food from rice, grains, beans, fruit, vegetables, pasta and more. After measuring, fill with water and swirl to clean. Strain then pour into your pot, pan, or dish. The convenient size is easy to hold with one hand and is compact to fit into a kitchen cabinet or pantry. Dishwasher safe and food safe.',
  'FREETOO Airsoft Gloves Men Tactical Gloves for Hiking Cycling Climbing Outdoor Camping Sports (Not Support Screen Touch).'
]
texts = [language_context] + item_metadata

inputs = tokenizer(texts, padding=True, truncation=True, max_length=512, return_tensors="pt")

# Get the embeddings
with torch.no_grad():
    embeddings = model(**inputs, return_dict=True).last_hidden_state[:, 0]
    embeddings = embeddings / embeddings.norm(dim=1, keepdim=True)

print(embeddings[0] @ embeddings[1])    # tensor(0.8564)
print(embeddings[0] @ embeddings[2])    # tensor(0.5741)
```

## Citation

If you find Amazon Reviews 2023 dataset, BLaIR checkpoints, Amazon-C4 dataset, or our scripts/code helpful, please cite the following paper.

```bibtex
@article{hou2024bridging,
  title={Bridging Language and Items for Retrieval and Recommendation},
  author={Hou, Yupeng and Li, Jiacheng and He, Zhankui and Yan, An and Chen, Xiusi and McAuley, Julian},
  journal={arXiv preprint arXiv:2403.03952},
  year={2024}
}
```

## Contact

Please let us know if you encounter a bug or have any suggestions/questions by [filling an issue](https://github.com/hyp1231/AmazonReview2023/issues/new) or emailing Yupeng Hou at [yphou@ucsd.edu](mailto:yphou@ucsd.edu).