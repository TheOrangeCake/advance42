from pydantic import BaseModel
import numpy as np
from .b1_part import STATE


class LogitPicker(BaseModel):

    @staticmethod
    def pick(next_logits: list[float]) -> tuple[STATE, int]:
        selected_logit = int(np.array(next_logits).argmax())
        state = STATE.NAME
        return state, selected_logit
