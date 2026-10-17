from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
import numpy as np
from .a2_file_handler import FunctionData
from .b1_part import STATE


class LogitPicker(BaseModel):
    """Pick next logits, function names and parameters. Also define state"""

    f_name: list[int] = []
    f_matched: list[FunctionData] = []

    def pick_next_logit(self, next_logits: list[float]) -> tuple[STATE, int]:
        """

        """
        selected_logit = int(np.array(next_logits).argmax())
        state = STATE.NAME
        return state, selected_logit

    def pick_function_name(
        self,
        selected_logit: int,
        functions: list[FunctionData],
        llm: Small_LLM_Model
    ) -> tuple[STATE, FunctionData | None]:
        """

        """
        self.f_name.append(selected_logit)
        function_name = llm.decode(self.f_name)

        if (function_name == "None"):
            return STATE.DONE, None

        # check if function name already match from the list
        self.f_matched = list(filter(
            lambda f: f.name.startswith(function_name),
            functions
        ))
        print(self.f_matched)
        return STATE.NAME, self.f_matched[0]

        # if len(matches) == 1:
        #     name_params_part = OutputPart.name_params_to_logits(matches[0], llm)
        #     output.extend(name_params_part)

        #     prompt.extend(name_params_part)
        #     state = STATE.PARAMS
