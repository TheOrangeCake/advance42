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

        self.functions_json = self.__function_list_to_string(functions)
        start = OutputPart.name_part_to_string(prompt)

        self.base_prompt = (
            'You are a function-calling assistant with access to the '
            'following tools:\n' +
            self.functions_json + '\n'
            '\n'
            '## Task\n'
            'Select from the provided tools the one that will solve '
            'the input.\n'
            '\n'
            '## Process\n'
            '1. Read the input\n'
            '2. Read each tool description\n'
            '3. Select the fitting tool\n'
            '4. Fill the expected output slots with relevant data from '
            'the tool and the input\n'
            '\n'
            '## Constraints\n'
            '- Only select a tool from the provided list\n'
            '- If no tool fits, fill the output slots with '
            '"None" for string type or None for other types\n'
            '- Format the response in the output shape. '
            'Example of output shape:\n' +
            self.output_shape + '\n'
            '\n'
            'Input: "' + prompt + '"\n'
            '\n' + start
        )
        # print(self.base_prompt)
        self.encoded_base_prompt = llm.encode(self.base_prompt)[0].tolist()
        return self.encoded_base_prompt

    def __function_list_to_string(self, functions: list[FunctionData]) -> str:
        """Convert defined function list to a json string.

        Args:
            functions (list[FunctionData]) : list of defined functions

        Returns:
            str: representation of defined list in json string format
        """

        res = [function.model_dump_json() for function in functions]
        return "[" + ",\n".join(res) + "]"
