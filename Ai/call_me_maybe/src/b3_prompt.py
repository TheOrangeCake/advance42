from pydantic import BaseModel
from .a2_file_handler import FunctionData

class Prompt(BaseModel):
    """Prompt for LLM."""

    def craft_prompt(input: str, functions: list[FunctionData]) -> str:
        pass