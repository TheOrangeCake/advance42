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
        'Input: "Replace all digits in \\"abc 123 def 456\\" with X"\n'
        '{"source_string": "abc 123 def 456", "regex": "[0-9]+", '
        '"replacement": "X"}\n'
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
            'You are a function-calling assistant with access to the '
            'following functions:\n' +
            functions_json + '\n'
            '\n'
            '## Task\n'
            'Return the name of the function that solves the input.\n'
            '\n'
            '## Constraints\n'
            '- Only select a function from the provided list or "None"\n'
            '- If no function fits, return "None"\n'
            '\n'
            '## Examples\n' +
            self.name_examples + '\n'
            'Input: ' + json.dumps(u_input, ensure_ascii=False) + '\n'
            '{"function_name": "'
        )
        return llm.encode(name_prompt)[0].tolist()

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
            'You are a function-calling assistant. Fill the parameters '
            'of this function call:\n' +
            f.model_dump_json() + '\n'
            '\n'
            '## Constraints\n'
            '- Take the values from the input, if a parameter expects a '
            'pattern or a format (see its name and the function '
            'description), write a value in that form\n'
            '- Do not compute the result of the function\n'
            '- Respect the parameter type\n'
            '\n'
            '## Example\n' +
            self.params_example + '\n'
            'Input: ' + json.dumps(u_input, ensure_ascii=False) + '\n'
            '{'
        )
        return llm.encode(params_prompt)[0].tolist()

    def __function_list_to_string(self, functions: list[FunctionData]) -> str:
        """Convert defined function list to a json string.

        Args:
            functions (list[FunctionData]) : list of defined functions

        Returns:
            str: representation of defined list in json string format
        """

        res = [f.model_dump_json() for f in functions]
        return "[" + ",\n".join(res) + "]"
