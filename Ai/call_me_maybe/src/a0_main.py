from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler, FunctionData, InputPrompt
from .z1_utils import Utils
from .b3_prompt import PromptBuilder
from .b2_picker import LogitPicker
from .b1_part import STATE, OutputPart
from pydantic import BaseModel, ConfigDict, Field
from argparse import Namespace
from llm_sdk import Small_LLM_Model
import sys


class Main(BaseModel):
    """Program main entrance."""
    model_config = ConfigDict(arbitrary_types_allowed=True)

    args: Namespace = Field(default_factory=lambda: ArgParser().parse(sys.argv[1:]))
    functions: list[FunctionData] = []
    inputs: list[InputPrompt] = []
    vocab: dict[int, str] = {}
    llm: Small_LLM_Model = Field(default_factory=lambda: Small_LLM_Model())

    def __call_llm(self, u_input: str) -> str:
        """Main program flow.

        Args:
            prompt: user input

        Returns:
            str: llm output
        """
        # Build the base prompt
        p_builder = PromptBuilder()
        prompt = p_builder.craft_base_prompt(u_input, self.functions, self.llm)

        picker = LogitPicker()

        # Initialize output with first part of output Json
        output = OutputPart.name_part_to_logits(u_input, self.llm)

        prompt_len_save = len(prompt)
        state = STATE.NAME
        turn = 0
        while turn < 12 and state != STATE.DONE:
            # Get next logits, select and update prompt
            next_logits = self.llm.get_logits_from_input_ids(prompt)
            selected_logit = picker.pick_next_logit(next_logits)
            prompt.append(selected_logit)

            # Check if output match defined functions
            if state == STATE.NAME:
                state, f = picker.pick_function_name(
                    selected_logit,
                    self.functions,
                    self.llm
                )
                if f is None:
                    return OutputPart.get_none_output(u_input)
                elif state == STATE.PARAMS:
                    forced_name_part = OutputPart.name_params_to_logits(f[0], self.llm)
                    output.extend(forced_name_part)
                    prompt = prompt[:prompt_len_save - 1]
                    prompt.extend(forced_name_part)

                    # print(self.llm.decode(prompt))
                    print(self.llm.decode(output))
                    state = STATE.DONE

            turn = turn + 1
        # print(llm.decode(output))

    def run(self) -> None:
        """Main program flow.

        Args:
            None

        Returns:
            None
        """
        self.functions = FileHandler.read_functions(self.args.functions)
        if not self.functions:
            Utils.error("Empty function definition list")

        self.inputs = FileHandler.read_inputs(self.args.inputs)
        if not self.inputs:
            Utils.error("Empty input list")

        FileHandler.validate_output_file(self.args.outputs)

        vocab_path = self.llm.get_path_to_vocab_file()
        self.vocab = FileHandler.read_vocab(vocab_path)

        outputs = []
        for input in self.inputs:
            outputs.append(self.__call_llm(input.prompt))

        # TODO: write outputs to file
        # res = "[" + ",\n".join(outputs) + "]"
