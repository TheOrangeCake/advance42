import json
from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from .a2_file_handler import FunctionData


class PromptBuilder(BaseModel):
    """Build prompt for LLM."""

    name_examples: str = (
        'Input: "Reverse the string \'hello\'"\n'
        '{"function_name": "fn_reverse_string"}\n'
        '\n'
        'Input: "Cook a burger"\n'
        '{"function_name": "None"}\n'
    )
    params_example: str = (
        'Input: "Reverse the string \'hello\'"\n'
        '{"s": "hello"}\n'
        '\n'
        'Input: "Replace all vowels in \'HELLO world\' with _"\n'
        '{"source_string": "HELLO world", "regex": "[aeiouAEIOU]", '
        '"replacement": "_"}\n'
    )

    def build_name_prompt(
        self,
        u_input: str,
        functions: list[FunctionData],
        llm: Small_LLM_Model
    ) -> list[int]:
        """Create the function name prompt and return encoded version.

        Args:
            u_input (str): user input
            functions (list[FunctionData]) : list of defined functions
            llm (Small_LLM_Model): instance of llm

        Returns:
            list[int]: encoded prompt
        """
        functions_json = self.__function_list_to_string(functions)

        name_prompt = (
            'Functions:\n' +
            functions_json + '\n'
            'Pick the function that solves the input, or "None" if none '
            'fits.\n'
            '\n' +
            self.name_examples + '\n'
            'Input: ' + json.dumps(u_input, ensure_ascii=False) + '\n'
            '{"function_name": "'
        )
        name_prompt_ids: list[int] = llm.encode(name_prompt)[0].tolist()
        return name_prompt_ids

    def build_params_prompt(
        self,
        u_input: str,
        f: FunctionData,
        llm: Small_LLM_Model
    ) -> list[int]:
        """Create the params prompt for the chosen function and return
        encoded version. Ends on the opening brace of the params object.

        Args:
            u_input (str): user input
            f (FunctionData): chosen function
            llm (Small_LLM_Model): instance of llm

        Returns:
            list[int]: encoded prompt
        """
        params_prompt = (
            'Function: ' + f.model_dump_json() + '\n'
            'Take the values from the input. A parameter that expects a '
            'pattern or a format gets a value in that form. Do not compute '
            'the result.\n'
            '\n' +
            self.params_example + '\n'
            'Input: ' + json.dumps(u_input, ensure_ascii=False) + '\n'
            '{'
        )
        params_prompt_ids: list[int] = llm.encode(params_prompt)[0].tolist()
        return params_prompt_ids

    def __function_list_to_string(self, functions: list[FunctionData]) -> str:
        """Convert defined function list to a json string.

        Args:
            functions (list[FunctionData]) : list of defined functions

        Returns:
            str: representation of defined list in json string format
        """

        res = [
            '{"function_name": "' +
            f.name +
            '", "description": "' +
            f.description +
            '"}'
            for f in functions
        ]
        return "[" + ",\n".join(res) + "]"
