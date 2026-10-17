from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
import numpy as np
import sys
from .a2_file_handler import FunctionData
from .b1_part import STATE


class LogitPicker(BaseModel):
    """Pick next logits, function names and parameters. Also define state"""

    f_name: list[int] = []
    f_matched: list[FunctionData] = []
    f_matched_old: list[FunctionData] = []

    def pick_next_logit(self, next_logits: list[float]) -> int:
        """

        """
        selected_logit = int(np.array(next_logits).argmax())
        return selected_logit

    def pick_function_name(
        self,
        selected_logit: int,
        functions: list[FunctionData],
        llm: Small_LLM_Model
    ) -> tuple[STATE, list[FunctionData] | None]:
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

        matched_len = len(self.f_matched)
        if matched_len == 1:
            return STATE.PARAMS, [self.f_matched[0]]
        elif matched_len == 0:
            if not self.f_matched_old:
                return STATE.DONE, None

            shortest_len = sys.maxsize
            for f in self.f_matched_old:
                new_len = len(f.name)
                if new_len < shortest_len:
                    f_shortest = f
                    shortest_len = new_len
            return STATE.PARAMS, [f_shortest]

        self.f_matched_old = self.f_matched
        return STATE.NAME, []
