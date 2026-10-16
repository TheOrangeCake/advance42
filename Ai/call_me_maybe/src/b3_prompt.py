from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from .a2_file_handler import FunctionData
from .b1_part import OutputPart


# qwen-ai.com/prompts/
class PromptBuilder(BaseModel):
    """Build prompt for LLM."""

    functions_json: str = ""
    base_prompt: str = ""
    encoded_base_prompt: list[int] = []
    output_shape: str = (
        '{\n'
        '\t"prompt": "Reverse the string \'hello\'",\n'
        '\t"name": "fn_reverse_string",\n'
        '\t"parameters": {"s": "hello"}\n'
        '}'
    )

    def craft_base_prompt(
        self,
        prompt: str,
        functions: list[FunctionData],
        llm: Small_LLM_Model
    ) -> list[int]:
        """Create the base prompt and return encoded version.

        Args:
            prompt (str): user input
            functions (list[FunctionData]) : list of defined functions
            llm (Small_LLM_Model): instance of llm

        Returns:
            list[int]: encoded prompt
        """

        self.functions_json = self.__defined_functions_to_string(functions)
        start = OutputPart.name_part_to_string(prompt)

        self.base_prompt = (
            'Select from the list of defined functions the function '
            'that will solve the input.\n'
            'Fill the slots with the name of the selected function '
            'and its parameters.\n'
            'If no function fits, use None in all slots.\n'
            '\n'
            'List of defined functions: ' + self.functions_json + '\n'
            '\n'
            'Example of output shape:\n' + self.output_shape + '\n'
            '\n'
            'Input: "' + prompt + '"\n'
            '\n' + start
        )
        self.encoded_base_prompt = llm.encode(self.base_prompt)[0].tolist()
        return self.encoded_base_prompt

    def __defined_functions_to_string(self, functions: list[FunctionData]) -> str:
        res = "["
        for function in functions:
            res = res + function.model_dump_json() + ",\n"
        return res + "]"
            