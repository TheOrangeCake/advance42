from pydantic import BaseModel
import numpy as np
from .b1_part import STATE

class LogitPicker(BaseModel):

    @staticmethod
    def pick(next_logits: torch.Tensor, output: list[int], prompt: list[int]):
        selected_logit = int(np.array(next_logits).argmax())
        output.append(selected_logit)
        prompt.append(selected_logit)
        state = STATE.NAME
        return output, prompt, state, selected_logit
