from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler
from .z1_utils import Utils
from .b3_prompt import PromptBuilder
from .b2_picker import LogitPicker, STATE, OutputPart
from pydantic import BaseModel
from llm_sdk import Small_LLM_Model
import sys


class Main(BaseModel):
    """Program main entrance."""

    @staticmethod
    def run() -> None:
        """Main program flow.

        Args:
            None

        Returns:
            None
        """

        arguments = ArgParser().parse(sys.argv[1:])

        functions = FileHandler.read_functions(arguments.functions)
        if not functions:
            Utils.error("Empty function definition list")

        inputs = FileHandler.read_inputs(arguments.inputs)
        if not inputs:
            Utils.error("Empty input list")

        FileHandler.validate_output_file(arguments.outputs)

        llm = Small_LLM_Model()

        vocab_path = llm.get_path_to_vocab_file()
        vocab = FileHandler.read_vocab(vocab_path)

        outputs = []
        for input in inputs:
            # Build the base prompt
            p_builder = PromptBuilder()
            prompt = p_builder.craft_base_prompt(input.prompt, functions, llm)
    
            output = OutputPart.name_part_to_logits(input.prompt, llm)

            state = STATE.NAME
            turn = 0
            while turn < 30 and state != STATE.DONE:
                next_logits = llm.get_logits_from_input_ids(prompt)
                output, prompt, state, next_logit = LogitPicker.pick(next_logits, output, prompt)
                print(vocab[next_logit])
                turn = turn + 1
            print(llm.decode(output))

