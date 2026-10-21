from pydantic import BaseModel, ConfigDict
from llm_sdk import Small_LLM_Model
from typing import Literal
import numpy as np
import math
import string
from .a2_file_handler import FunctionData, AcceptDataTypes
from .b0_state import STATE, StateManager


type NumberType = Literal["integer", "float"]


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
        """Pick the logit with the highest score, constrained by the
        current params type.

        Args:
            next_logits (list[float]): logits score from llm

        Returns:
            int: id of the picked logit
        """
        if self.manager.state == STATE.PARAMS:
            match self.f_param_type:
                case "integer":
                    selected_logit = self.__handle_number(
                        next_logits, "integer"
                    )

                case "number":
                    selected_logit = self.__handle_number(next_logits, "float")

                case "boolean":
                    # force true or false
                    true_value = next_logits[self.boolean_ids["true"]]
                    false_value = next_logits[self.boolean_ids["false"]]
                    selected = "true" if true_value > false_value else "false"
                    return self.boolean_ids[selected]

                case "string":
                    selected_logit = self.__handle_string(next_logits)

                case _:
                    selected_logit = int(np.array(next_logits).argmax())
        else:
            selected_logit = int(np.array(next_logits).argmax())
        return selected_logit

    def __handle_string(self, next_logits: list[float]) -> int:
        """Pick the highest scored token that keeps the value a valid
        Json string.

        Args:
            next_logits (list[float]): logits score from llm

        Returns:
            int: id of the picked token
        """
        sorted_ids = np.argsort(next_logits, descending=True)
        current_param = self.llm.decode(self.f_param_value)
        for token_id in sorted_ids:
            new_str = self.llm.decode(self.f_param_value + [int(token_id)])
            if new_str == current_param:
                continue
            if self.__is_string_valid(new_str):
                return int(token_id)
        raise ValueError(f"No valid 'string' token after '{current_param}'")

    def __is_string_valid(self, new_str: str) -> bool:
        """Check if a string is a valid start of a Json string value.

        Args:
            new_str (str): current value with the candidate token appended

        Returns:
            bool: True if the string can still become a valid Json string
        """
        # one space allowed at the start only
        if new_str.startswith(" "):
            new_str = new_str[1:]
        if not new_str.startswith('"'):
            return False
        _, valid = self.__scan_string(new_str[1:])
        return valid

    def __handle_number(
        self,
        next_logits: list[float],
        nb_type: NumberType
    ) -> int:
        """Pick the highest scored token that keeps the value a valid number.

        Args:
            next_logits (list[float]): logits score from llm
            nb_type (NumberType): integer or float

        Returns:
            int: id of the picked token, a number token or an end token
        """
        if nb_type == "integer":
            number_ids = self.integer_ids
            number_check = self.__is_int_valid
        else:
            number_ids = self.float_ids
            number_check = self.__is_float_valid

        sorted_ids = np.argsort(next_logits, descending=True)
        current_param = self.llm.decode(self.f_param_value)
        for token_id in sorted_ids:
            if token_id in number_ids:
                token_str = number_ids[token_id]
                if number_check(current_param + token_str):
                    return int(token_id)
            elif token_id in self.end_ids:
                if self.__is_end_valid(current_param, nb_type):
                    return int(token_id)
        raise ValueError(
            f"No valid '{nb_type}' token after '{current_param}'"
        )

    def __is_float_valid(self, new_str: str) -> bool:
        """Check if a string is a valid start of a Json number.

        Args:
            new_str (str): current value with the candidate token appended

        Returns:
            bool: True if the string can still become a valid number
        """
        # one space allowed at the start only
        if new_str.startswith(" "):
            new_str = new_str[1:]
        if " " in new_str:
            return False

        # scientific notation case
        for candidate in (new_str, new_str + "0"):
            try:
                if math.isfinite(float(candidate)):
                    return True
            except ValueError:
                pass
        return False

    def __is_int_valid(self, new_str: str) -> bool:
        """Check if a string is a valid start of a Json integer.

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

    def __is_end_valid(self, current_str: str, nb_type: NumberType) -> bool:
        """Check if a string is a complete Json number, ready to be closed.

        Args:
            current_str (str): current value, without the end token
            nb_type (NumberType): integer or float

        Returns:
            bool: True if the string is a complete integer or finite float
        """
        try:
            if nb_type == "integer":
                int(current_str)
                return True
            return math.isfinite(float(current_str))
        except ValueError:
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
            self.manager.state = self.__set_next_params()
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
            self.manager.state = self.__set_next_params()
            return

        self.f_matched_old = self.f_matched
        self.manager.state = STATE.NAME
        return

    def __set_next_params(self) -> STATE:
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
        """Add the picked token to the current params value.

        Args:
            selected_logit (int): id of the picked logit

        Returns:
            None
        """
        # call __set_next_params() for state if current params done
        match self.f_param_type:
            case "string":
                self.f_param_value.append(selected_logit)
                decoded = self.llm.decode(self.f_param_value)
                content = decoded.lstrip(" ")[1:]
                quote_pos, _ = self.__scan_string(content)
                if quote_pos >= 0:
                    value = ' "' + content[:quote_pos + 1]
                    self.f_param_value = self.llm.encode(value)[0].tolist()
                    self.manager.state = self.__set_next_params()
                    return
            case "boolean":
                self.f_param_value.append(selected_logit)
                self.manager.state = self.__set_next_params()
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
                    self.manager.state = self.__set_next_params()
                    return
            case "number":
                if selected_logit in self.float_ids:
                    self.f_param_value.append(selected_logit)
                    return
                if selected_logit in self.end_ids:
                    # remove the leading 0
                    decoded = self.llm.decode(self.f_param_value)

                    value = ' ' + str(float(decoded))
                    self.f_param_value = self.llm.encode(value)[0].tolist()
                    self.manager.state = self.__set_next_params()
                    return

    def __scan_string(self, text: str) -> tuple[int, bool]:
        """Find the first unescaped double quote in a Json string value and
        check the content before it is valid Json.

        Args:
            text (str): decoded string value, without the opening quote

        Returns:
            tuple[int, bool]: index of the closing quote, or -1 if not
                found, and True if the content is valid
        """
        i = 0
        while i < len(text):
            c = text[i]
            if c == '"':
                return i, True
            # No controlling like \n \t
            if ord(c) < 0x20:
                return -1, False
            if c == '\\':
                escaped = text[i + 1:i + 2]
                if escaped == "":
                    return -1, True
                # geeksforgeeks.org/javascript/how-to-escape-strings-in-json/
                # escape characters
                if escaped not in '"\\/bfnrtu':
                    return -1, False
                # special character
                if escaped == "u":
                    hex_part = text[i + 2:i + 6]
                    if any(h not in string.hexdigits for h in hex_part):
                        return -1, False
                    i += 2 + len(hex_part)
                    continue
                i += 2
                continue
            i += 1
        return -1, True
