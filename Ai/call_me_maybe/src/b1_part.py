from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from enum import Enum


class STATE(Enum):
    NAME = 1
    PARAMS = 2
    DONE = 3


class OutputPart(BaseModel):

    @staticmethod
    def name_part_to_string(prompt: str) -> str:
        """Return the string of the output in STATE.NAME state.

        Args:
            prompt (str): user input

        Returns:
            str: STATE.NAME state in string
        """

        return '{\n\t"prompt": "' + prompt + '",\n\t"name": "'

    @staticmethod
    def name_part_to_logits(prompt: str, llm: Small_LLM_Model) -> list[int]:
        """Return the encoded version of the output in STATE.NAME state.

        Args:
            prompt (str): user input

        Returns:
            list[int]: STATE.NAME state encoded
        """

        start_string = OutputPart.name_part_to_string(prompt)
        start_string_encoded: list[int] = llm.encode(start_string)[0].tolist()
        return start_string_encoded
