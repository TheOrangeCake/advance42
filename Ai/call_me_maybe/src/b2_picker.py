from pydantic import BaseModel, ConfigDict
from llm_sdk import Small_LLM_Model
import numpy as np
from .a2_file_handler import FunctionData, AcceptDataTypes
from .b0_state import STATE, StateManager
from .z1_utils import Utils


class LogitPicker(BaseModel):
    """Pick next logits, function names and parameters. Also define state"""
    model_config = ConfigDict(arbitrary_types_allowed=True)

    manager: StateManager
    boolean_ids: dict[str, int]
    integer_ids: dict[int, str]
    float_ids: dict[int, str]
    end_ids: dict[int, str]
    llm: Small_LLM_Model
    f_name: list[int] = []
    f_matched: list[FunctionData] = []
    f_matched_old: list[FunctionData] = []
    f_chosen: FunctionData | None = None
    f_param_name: str | None = None
    f_param_type: AcceptDataTypes | None = None
    f_current_params_index: int = -1
    f_param_value: list[int] = []

    def __init__(
        self,
        manager: StateManager,
        boolean_ids: dict[str, int],
        token_ids: list[dict[int, str]],
        llm: Small_LLM_Model
    ) -> None:
        """Init LogitPicker with state manager.

        Args:
            manager (StateManager): state manager
            boolean_ids (dict[str, int]): boolean ids
            token_ids (list[dict[int, str]]): integer, float and
                end token ids, in this order
            llm (Small_LLM_Model): instance of llm

        Returns:
            None
        """
        integer_ids, float_ids, end_ids = token_ids

        super().__init__(
            manager=manager,
            boolean_ids=boolean_ids,
            integer_ids=integer_ids,
            float_ids=float_ids,
            end_ids=end_ids,
            llm=llm
        )

    def pick_next_logit(self, next_logits: list[float]) -> int:
        """Pick the logit with the highest score.

        Args:
            next_logits (list[float]): logits score from llm

        Returns:
            int: id of the picked logit
        """
        if self.manager.state == STATE.PARAMS:
            match self.f_param_type:
                case "number":
                    # placeholder
                    selected_logit = int(np.array(next_logits).argmax())

                case "integer":
                    sorted_ids = np.argsort(next_logits, descending=True)
                    current_param = self.llm.decode(self.f_param_value)
                    for token_id in sorted_ids:
                        if token_id in self.integer_ids:
                            token_str = self.integer_ids[token_id]
                            if self.is_int_valid(current_param + token_str):
                                return int(token_id)
                        elif token_id in self.end_ids:
                            if self.is_end_valid(current_param):
                                return int(token_id)
                    Utils.error(
                        f"No valid integer token after '{current_param}'"
                    )

                case "boolean":
                    # force true or false
                    true_value = next_logits[self.boolean_ids["true"]]
                    false_value = next_logits[self.boolean_ids["false"]]
                    selected = "true" if true_value > false_value else "false"
                    return self.boolean_ids[selected]

                case _:  # case string included
                    selected_logit = int(np.array(next_logits).argmax())
        else:
            selected_logit = int(np.array(next_logits).argmax())
        return selected_logit

    def is_int_valid(self, new_str: str) -> bool:
        """Check if a string is a valid start of a Json integer.

        Accept one leading space and a lone "-", reject leading 0 like "05"
        or "-05".

        Args:
            new_str (str): current value with the candidate token appended

        Returns:
            bool: True if the string can still become a valid integer
        """
        # one space allowed at the start only
        if new_str.startswith(" "):
            new_str = new_str[1:]
        if " " in new_str:
            return False

        # if only " " or "-"
        if new_str == "" or new_str == "-":
            return True

        # is valid int
        try:
            int(new_str)
            return True
        except Exception:
            return False

    def is_end_valid(self, current_str: str) -> bool:
        """Check if a string is a complete Json integer, ready to be closed.

        Args:
            current_str (str): current value, without the end token

        Returns:
            bool: True if the string is a complete integer
        """
        try:
            int(current_str)
            return True
        except Exception:
            return False

    def pick_function_name(
        self,
        selected_logit: int,
        functions: list[FunctionData]
    ) -> None:
        """Match the picked logits with defined function names.

        Set state to NAME if still more than 1 function match, NEXT_PARAMS
        or DONE when a function is chosen, NONE if nothing match.

        Args:
            selected_logit (int): id of the picked logit
            functions (list[FunctionData]): list of defined functions

        Returns:
            None
        """
        self.f_name.append(selected_logit)
        function_name = self.llm.decode(self.f_name)

        if (function_name == "None"):
            self.manager.state = STATE.NONE
            return

        # check if function name already match from the list
        self.f_matched = list(filter(
            lambda f: f.name.startswith(function_name),
            functions
        ))

        matched_len = len(self.f_matched)
        if matched_len == 1:
            self.f_chosen = self.f_matched[0]
            self.manager.state = self.set_next_params()
            return
        elif matched_len == 0:
            prev_name = self.llm.decode(self.f_name[:-1])
            f_exact = None
            for f in self.f_matched_old:
                if f.name == prev_name:
                    f_exact = f
                    break
            if f_exact is None:
                self.manager.state = STATE.NONE
                return

            self.f_chosen = f_exact
            self.manager.state = self.set_next_params()
            return

        self.f_matched_old = self.f_matched
        self.manager.state = STATE.NAME
        return

    def set_next_params(self) -> STATE:
        """Move to the next params of the chosen function.

        Update f_param_name and f_param_type, or set them to None
        if there is no more params.

        Args:
            None

        Returns:
            STATE: NEXT_PARAMS, or DONE if there is no more params
        """
        assert self.f_chosen is not None
        params = list(self.f_chosen.parameters.items())

        self.f_current_params_index += 1
        if self.f_current_params_index >= len(params):
            self.f_param_name = None
            self.f_param_type = None
            return STATE.DONE

        name, item = params[self.f_current_params_index]
        self.f_param_name = name
        self.f_param_type = item.type
        return STATE.NEXT_PARAMS

    def pick_params(
        self,
        selected_logit: int
    ) -> None:
        """
        """
        # call set_next_params() for state if current params done
        match self.f_param_type:
            case "string":
                self.f_param_value.append(selected_logit)
                decoded = self.llm.decode(self.f_param_value)
                quote_pos = self.__find_closing_quote(decoded)
                if quote_pos >= 0:
                    value = decoded[:quote_pos + 1]
                    self.f_param_value = self.llm.encode(value)[0].tolist()
                    self.manager.state = self.set_next_params()
                    return
            case "boolean":
                self.f_param_value.append(selected_logit)
                self.manager.state = self.set_next_params()
                return
            case "integer":
                if selected_logit in self.integer_ids:
                    self.f_param_value.append(selected_logit)
                    return
                if selected_logit in self.end_ids:
                    # remove the leading 0
                    decoded = self.llm.decode(self.f_param_value)
                    value = ' ' + str(int(decoded))
                    self.f_param_value = self.llm.encode(value)[0].tolist()
                    self.manager.state = self.set_next_params()
                    return

    def __find_closing_quote(self, text: str) -> int:
        """Find the first unescaped double quote in a Json string value.

        Also handle edge cases like \\\\ or substring like s\"substr\"tring".

        Args:
            text (str): decoded string value, without the opening quote

        Returns:
            int: index of the closing quote, or -1 if not found
        """
        escaped = False
        for i, c in enumerate(text):
            if escaped:
                escaped = False
            elif c == '\\':
                escaped = True
            elif c == '"':
                return i
        return -1
