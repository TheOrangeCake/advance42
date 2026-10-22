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


MAX_TURN = 42


class Main(BaseModel):
    """Program main entrance."""
    model_config = ConfigDict(arbitrary_types_allowed=True)

    args: Namespace = Field(
        default_factory=lambda: ArgParser().parse(sys.argv[1:])
    )
    functions: list[FunctionData] = []
    inputs: list[InputPrompt] = []
    vocab: dict[str, int] = {}
    boolean_ids: dict[str, int] = {}
    integer_ids: dict[int, str] = {}
    float_ids: dict[int, str] = {}
    end_ids: dict[int, str] = {}

    def run(self) -> None:
        """Main program flow.

        Args:
            None

        Returns:
            None
        """
        try:
            self.functions = FileHandler.read_functions(self.args.functions)
            if not self.functions:
                Utils.error("Empty function definition list")

            self.inputs = FileHandler.read_inputs(self.args.inputs)
            if not self.inputs:
                Utils.error("Empty input list")

            FileHandler.validate_output_file(self.args.outputs)

            try:
                llm = Small_LLM_Model()
            except Exception:
                Utils.error("LLM init")

            vocab_path = llm.get_path_to_vocab_file()
            self.vocab = FileHandler.read_vocab(vocab_path)
            self.boolean_ids = {
                "true": self.vocab["true"],
                "false": self.vocab["false"]
            }
            self.integer_ids = {
                v: k for k, v in self.vocab.items()
                if k and all(c in "0123456789-" for c in k)
            }
            self.float_ids = {
                v: k for k, v in self.vocab.items()
                if k and all(c in "0123456789-+.eE" for c in k)
            }
            # Value starts right after '":', allow " " and " -" as first token
            for start in ("Ġ", "Ġ-"):
                text = start.replace("Ġ", " ")
                self.integer_ids[self.vocab[start]] = text
                self.float_ids[self.vocab[start]] = text
            # "Ġ" is a space, "Ċ" a newline
            self.end_ids = {
                v: k for k, v in self.vocab.items()
                if all(c in ",}ĊĠ" for c in k) and ("," in k or "}" in k)
            }

            outputs = []
            for input in self.inputs:
                try:
                    output = self.__call_llm(input.prompt, llm)
                except KeyboardInterrupt:
                    Utils.error("Keyboard interrupted")
                except Exception as e:
                    print(f"Error: {e}", file=sys.stderr)
                    output = OutputPart.get_error(input.prompt)
                print(output)  # debug
                outputs.append(output)

            res = "[\n" + ",\n".join(outputs) + "\n]"
            FileHandler.write_outputs(self.args.outputs, res)
            return
        except KeyboardInterrupt:
            Utils.error("Keyboard interrupted")
        except Exception as e:
            Utils.error(f"{e}")

    def __call_llm(self, u_input: str, llm: Small_LLM_Model) -> str:
        """Generate the function call Json of one user input.

        Args:
            u_input (str): user input
            llm (Small_LLM_Model): instance of LLM

        Returns:
            str: llm output
        """
        # Build the base prompt
        p_builder = PromptBuilder()
        prompt = p_builder.build_name_prompt(
            u_input, self.functions, llm
        )

        manager = StateManager()
        picker = LogitPicker(
            manager,
            self.boolean_ids,
            [self.integer_ids, self.float_ids, self.end_ids],
            llm
        )

        # Initialize output with first part of output Json
        output = OutputPart.build_head_ids(u_input, llm)

        prompt_len_save = len(prompt)
        for _ in range(MAX_TURN):
            if manager.state == STATE.NEXT_PARAMS:
                if picker.f_param_name is None or picker.f_param_type is None:
                    return OutputPart.get_none(u_input)
                isFirst = picker.f_current_params_index == 0
                params_ids = OutputPart.build_params_ids(
                    picker.f_param_name, picker.f_param_type, llm, isFirst
                )
                output.extend(params_ids)
                prompt.extend(params_ids)
                prompt_len_save = len(prompt)
                manager.state = STATE.PARAMS

            # Get next logits, select and update prompt
            next_logits = llm.get_logits_from_input_ids(prompt)
            selected_logit = picker.pick_next_logit(next_logits)
            prompt.append(selected_logit)

            # Check if output match defined functions
            if manager.state == STATE.NAME:
                picker.pick_function_name(selected_logit, self.functions)
                match manager.state:
                    case STATE.NONE:
                        return OutputPart.get_none(u_input)
                    case STATE.DONE:
                        if picker.f_chosen is None:
                            return OutputPart.get_none(u_input)
                        return OutputPart.get_no_params(
                            u_input, picker.f_chosen
                        )
                    case STATE.NEXT_PARAMS:
                        if picker.f_chosen is None:
                            return OutputPart.get_none(u_input)
                        forced_name_part = OutputPart.build_name_ids(
                            picker.f_chosen, llm
                        )
                        output.extend(forced_name_part)
                        # Switch to the params prompt, the head and name
                        # part only go to output
                        prompt = p_builder.build_params_prompt(
                            u_input, picker.f_chosen, llm
                        )
                        prompt_len_save = len(prompt)
                    case _:
                        pass

            if manager.state == STATE.PARAMS:
                picker.pick_params(selected_logit)
                state: STATE = manager.state
                if state == STATE.NEXT_PARAMS:
                    output.extend(picker.f_param_value)
                    prompt = prompt[:prompt_len_save]
                    prompt.extend(picker.f_param_value)
                    prompt_len_save = len(prompt)
                    picker.f_param_value = []
                if state == STATE.DONE:
                    output.extend(picker.f_param_value)
                    prompt = prompt[:prompt_len_save]
                    prompt.extend(picker.f_param_value)
                    tail_ids = OutputPart.build_tail_ids(llm)
                    output.extend(tail_ids)
                    return str(llm.decode(output))
        else:
            return OutputPart.get_none(u_input)
