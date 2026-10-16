from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
from .a2_file_handler import FunctionData


class PromptBuilder(BaseModel):
    """Build prompt for LLM."""

    base_prompt: str = ""
    encoded_prompt: list[int] = []
    output_shape: str = """
            {
                "prompt": ${input prompt},
                "name": ${defined function name from the list},
                "parameters": {${arg1}: ${value1}, ${arg2}: ${value2}, ...}
            }
        """

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

        self.base_prompt = f"""
            List of defined functions: {functions}.
            Input: {prompt}.
            Ouput shape: {self.output_shape}
            Select from the list of defined functions the function
            that will solve the input.
            Fill the insertable slots with name of function selected
            and it's parameter.
            If no function fit then use None in all insertable slots.
        """
        self.encoded_prompt = llm.encode(self.base_prompt)[0].tolist()
        return self.encoded_prompt
