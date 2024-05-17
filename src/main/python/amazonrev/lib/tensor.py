import torch


def tensor_to_string(tensor: torch.Tensor) -> str:
  """
  For use on 1D tensors i.e. lists.
  """
  tensor_string = f'[{','.join([str(t) for t in tensor.tolist()])}]'
  return tensor_string
