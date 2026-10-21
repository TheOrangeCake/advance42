from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
import json
from .a2_file_handler import FunctionData, AcceptDataTypes


class OutputPart(BaseModel):
    """Build the forced parts of Json output."""

    @staticmethod
    def get_none(u_input: str) -> str:
        """Get Json output when no tool matched.

        Args:
            u_input (str): user input

        Returns:
            str: Json string of output in case None
        """
        return (
            '\t{\n'
            '\t\t"prompt": ' + json.dumps(u_input, ensure_ascii=False) + ',\n'
            '\t\t"name": "None",\n'
            '\t\t"parameters": {}\n'
            '\t}'
        )

    @staticmethod
    def get_no_params(u_input: str, f: FunctionData) -> str:
        """Get Json output when matched tool has no params.

        Args:
            u_input (str): user input
            f (FunctionData): matched function

        Returns:
            str: Json string of output in case no params
        """
        return (
            '\t{\n'
            '\t\t"prompt": ' + json.dumps(u_input, ensure_ascii=False) + ',\n'
            '\t\t"name": "' + f.name + '",\n'
            '\t\t"parameters": {}\n'
            '\t}'
        )

    @staticmethod
    def build_head(u_input: str) -> str:
        """Get the start of Json output in string.

        Args:
            u_input (str): user input

        Returns:
            str: start of Json output with user input included
        """
        return (
            '\t{\n\t\t"prompt": ' +
            json.dumps(u_input, ensure_ascii=False) +
            ',\n\t\t"name": "'
        )

    @staticmethod
    def build_head_ids(u_input: str, llm: Small_LLM_Model) -> list[int]:
        """Get the encoded start of Json output.

        Args:
            u_input (str): user input
            llm (Small_LLM_Model): instance of llm

        Returns:
            list[int]: encoded start of Json output with user input included
        """
        start_string = OutputPart.build_head(u_input)
        start_string_encoded: list[int] = llm.encode(start_string)[0].tolist()
        return start_string_encoded

    @staticmethod
    def build_name(f: FunctionData) -> str:
        """Get the name and params opening of Json output in string.

        Args:
            f (FunctionData): matched function

        Returns:
            str: name and params opening of Json output
        """
        return (
            f.name +
            '",\n\t\t"parameters": {'
        )

    @staticmethod
    def build_name_ids(f: FunctionData, llm: Small_LLM_Model) -> list[int]:
        """Get the encoded name and params opening of Json output.

        Args:
            f (FunctionData): matched function
            llm (Small_LLM_Model): instance of llm

        Returns:
            list[int]: encoded name and params opening of Json output
        """
        params_string = OutputPart.build_name(f)
        params_encoded: list[int] = llm.encode(params_string)[0].tolist()
        return params_encoded

    @staticmethod
    def build_params(
        params: str,
        params_type: AcceptDataTypes,
        isFirst: bool
    ) -> str:
        """Get the params key of Json output in string.

        Args:
            params (str): params name
            params_type (AcceptDataTypes): params type
            isFirst (bool): first params of the function, no comma before

        Returns:
            str: params key of Json output
        """
        first = ', "'
        space = '": '
        if isFirst:
            first = '"'
        if params_type in ("number", "integer", "string"):
            space = '":'
        return first + params + space

    @staticmethod
    def build_params_ids(
        params: str,
        params_type: AcceptDataTypes,
        llm: Small_LLM_Model,
        isFirst: bool
    ) -> list[int]:
        """Get the encoded params key of Json output.

        Args:
            params (str): params name
            params_type (AcceptDataTypes): params type
            llm (Small_LLM_Model): instance of llm
            isFirst (bool): first params of the function, no comma before

        Returns:
            list[int]: encoded params key of Json output
        """
        params_string = OutputPart.build_params(params, params_type, isFirst)
        params_encoded: list[int] = llm.encode(params_string)[0].tolist()
        return params_encoded

    @staticmethod
    def build_tail() -> str:
        """Get the end of Json output in string.

        Args:
            None

        Returns:
            str: end of Json output
        """
        return '}\n\t}'

    @staticmethod
    def build_tail_ids(llm: Small_LLM_Model) -> list[int]:
        """Get the encoded end of Json output.

        Args:
            llm (Small_LLM_Model): instance of llm

        Returns:
            list[int]: encoded end of Json output
        """
        tail_string = OutputPart.build_tail()
        tail_encoded: list[int] = llm.encode(tail_string)[0].tolist()
        return tail_encoded
