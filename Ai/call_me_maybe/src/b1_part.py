from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from enum import Enum
from .a2_file_handler import FunctionData


class STATE(Enum):
    NAME = 1
    PARAMS = 2
    DONE = 3


class OutputPart(BaseModel):

    @staticmethod
    def get_none_output(prompt: str) -> str:
        """Get Json output when no tool matched.

        Args:
            prompt (str): user input

        Returns:
            str: Json string of output in case None
        """
        return (
            '{\n'
            '\t"prompt": "' + prompt + '",\n'
            '\t"name": "None",\n'
            '\t"parameters": {"None": "None"}\n'
            '}'
        )

    @staticmethod
    def name_part_to_string(prompt: str) -> str:
        """Get the start of Json output in string.

        Args:
            prompt (str): user input

        Returns:
            str: start of Json output with prompt included
        """

        return ('{\n\t"prompt": "' + prompt + '",\n\t"name": "')

    @staticmethod
    def name_part_to_logits(prompt: str, llm: Small_LLM_Model) -> list[int]:
        """Get the encoded start of Json output.

        Args:
            prompt (str): user input

        Returns:
            list[int]: encoded start of Json output with prompt included
        """

        start_string = OutputPart.name_part_to_string(prompt)
        start_string_encoded: list[int] = llm.encode(start_string)[0].tolist()
        return start_string_encoded

    @staticmethod
    def name_params_to_string(function: FunctionData) -> str:
        """Get the name and params of Json output in string.

        Args:
            prompt (str): user input

        Returns:
            str: name and params of Json output
        """
        return (
            function.name +
            '",\n\t"parameters": {"' +
            next(iter(function.parameters)) +
            ': '
        )

    @staticmethod
    def name_params_to_logits(f: FunctionData, llm: Small_LLM_Model) -> str:
        """Get the encoded first params of Json output.

        Args:
            prompt (str): user input

        Returns:
            str: encoded first params of Json output
        """

        params_string = OutputPart.name_params_to_string(f)
        params_encoded: list[int] = llm.encode(params_string)[0].tolist()
        return params_encoded
