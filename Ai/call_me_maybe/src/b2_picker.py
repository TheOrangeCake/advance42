from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from enum import Enum
import numpy as np


class STATE(Enum):
    NAME = 1
    PARAMS = 2
    DONE = 3

class LogitPicker(BaseModel):

    @staticmethod
    def pick(next_logits: torch.Tensor, output: list[int], prompt: list[int]):
        next_logit = int(np.array(next_logits).argmax())
        output.append(next_logit)
        prompt.append(next_logit)
        state = STATE.NAME
        return output, prompt, state
