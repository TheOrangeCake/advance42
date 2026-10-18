from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler, FunctionData, InputPrompt
from .z1_utils import Utils
from .b3_prompt import PromptBuilder
from .b2_picker import LogitPicker
from .b1_part import OutputPart
from .b0_state import STATE, StateManager
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
            u_input (str): user input

        Returns:
            str: llm output
        """
        # Build the base prompt
        p_builder = PromptBuilder()
        prompt = p_builder.build_base_prompt(u_input, self.functions, self.llm)

        manager = StateManager()
        picker = LogitPicker(manager)

        # Initialize output with first part of output Json
        output = OutputPart.build_head_ids(u_input, self.llm)

        prompt_len_save = len(prompt)
        turn = 0
        while turn < 12 and manager.state != STATE.DONE:
            if manager.state == STATE.NEXT_PARAMS:
                params = picker.f_param_name
                isFirst = picker.f_current_params_index == 0
                params_ids = OutputPart.build_params_ids(params, self.llm, isFirst)
                output.extend(params_ids)
                prompt.extend(params_ids)
                prompt_len_save = len(prompt)
                manager.state = STATE.PARAMS
                return self.llm.decode(output) # debug

            # Get next logits, select and update prompt
            next_logits = self.llm.get_logits_from_input_ids(prompt)
            selected_logit = picker.pick_next_logit(next_logits)
            prompt.append(selected_logit)

            # Check if output match defined functions
            if manager.state == STATE.NAME:
                picker.pick_function_name(
                    selected_logit,
                    self.functions,
                    self.llm
                )
                if manager.state == STATE.NONE:
                    return OutputPart.get_none(u_input)
                elif manager.state == STATE.DONE:
                    return OutputPart.get_no_params(u_input, picker.f_chosen)
                elif manager.state == STATE.NEXT_PARAMS:
                    forced_name_part = OutputPart.build_name_ids(picker.f_chosen, self.llm)
                    output.extend(forced_name_part)
                    prompt = prompt[:prompt_len_save]
                    prompt.extend(forced_name_part)
                    prompt_len_save = len(prompt)
            
            if manager.state == STATE.PARAMS:
                # Do params picker here
                pass

                

            turn = turn + 1

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
            output = self.__call_llm(input.prompt)
            print(output) # debug
            outputs.append(output)

        # TODO: write outputs to file
        # res = "[" + ",\n".join(outputs) + "]"
