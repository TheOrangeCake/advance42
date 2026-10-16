from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from enum import Enum


class STATE(Enum):
    NAME = 1
    PARAMS = 2
    DONE = 3


class OutputPart(BaseModel):

    @staticmethod
    def name_part_to_logits(prompt: str, llm: Small_LLM_Model) -> list[int]:
        start = '{\n\t"prompt": "' + prompt + '",\n\t"name": "'
        return llm.encode(start)[0].tolist()

    @staticmethod
    def name_part_to_string(prompt: str) -> str:
        return '{\n\t"prompt": "' + prompt + '",\n\t"name": "'
