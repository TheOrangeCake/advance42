from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler
from .z1_utils import Utils
from .b3_prompt import PromptBuilder
from .b2_picker import LogitPicker, STATE
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

        # Read functions def
        functions = FileHandler.read_functions(arguments.functions)
        if not functions:
            Utils.error("Empty function definition list")

        # Read input prompts
        inputs = FileHandler.read_inputs(arguments.inputs)
        if not inputs:
            Utils.error("Empty input list")

        # Check output file validity
        FileHandler.validate_output_file(arguments.outputs)

        # Init LLM
        llm = Small_LLM_Model()

        # Get vocab file
        vocab_path = llm.get_path_to_vocab_file()
        vocab = FileHandler.read_vocab(vocab_path)

        # Call LLM for each input prompt
        outputs = []
        for input in inputs:
            # Build the base prompt
            p_builder = PromptBuilder()
            prompt = p_builder.craft_base_prompt(input.prompt, functions, llm)
    
            output = []
            state = STATE.NAME
            turn = 0
            while turn < 30 or state != STATE.DONE:

                next_logits = llm.get_logits_from_input_ids(prompt)
                output, prompt, state = LogitPicker.pick(next_logits, output, prompt)
                print(llm.decode(output))
                turn = turn + 1
