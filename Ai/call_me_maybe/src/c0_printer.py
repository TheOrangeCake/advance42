from pydantic import BaseModel, ConfigDict
from enum import Enum
from typing import Literal
from datetime import datetime


type GenMode = Literal["template", "generated", "autoCompleted"]
WIDTH = 130


class COLOR(Enum):
    """Color for terminal printing."""
    RED = "\033[31m"
    GREEN = "\033[32m"
    YELLOW = "\033[33m"
    BLUE = "\033[34m"
    CYAN = "\033[36m"
    BOLD = "\033[1m"
    DIM = "\033[2m"
    RESET = "\033[0m"


class Printer(BaseModel):
    """Visualize the generation"""
    model_config = ConfigDict(arbitrary_types_allowed=True)

    u_input: str
    enable: bool
    start_time: datetime
    logit: str = ""
    logits: dict[str, float] = {}
    output: str = ""
    pending: str = ""
    line_count: int = 0

    def __init__(
        self,
        u_input: str,
        enable: bool,
    ) -> None:
        """Init printer with input.

        Args:
            u_input (str): input prompt
            enable (bool): viszalize mode on or off

        Return:
            None
        """
        super().__init__(
            u_input=u_input,
            enable=enable,
            start_time=datetime.now()
        )

    def print_snapshot(
        self
    ) -> None:
        """
        """
        if not self.enable:
            return

        input_line = (
            COLOR.YELLOW.value + self.u_input + COLOR.RESET.value
        )

        line_indent = (
            COLOR.YELLOW.value + '-> ' + COLOR.RESET.value
        )

        logits_line = ", ".join(
            [
                f"[ {COLOR.CYAN.value}{repr(k)}{COLOR.RESET.value}: "
                f"{format(v, '.2f')} ]"
                for k, v in self.logits.items()
            ]
        )
        top_line = (
            line_indent +
            COLOR.BLUE.value + 'Top tokens: \t' + COLOR.RESET.value +
            logits_line
        )

        logit_line = (
            line_indent +
            COLOR.BLUE.value + 'Selected token:\t' + COLOR.RESET.value +
            self.logit
        )

        current_time = datetime.now()
        elapsed = current_time - self.start_time
        (minutes, seconds) = divmod(elapsed.seconds, 60)
        time_line = (
            line_indent +
            COLOR.BLUE.value + 'Elapsed time:\t' + COLOR.RESET.value +
            str(minutes) + 'm ' + str(seconds) + 's'
        )

        output_line = (
            line_indent +
            COLOR.BLUE.value + 'Output:\n' + COLOR.RESET.value +
            self.output +
            COLOR.GREEN.value + self.pending + COLOR.RESET.value
        )

        whole_line = (
            input_line + '\n' +
            top_line + '\n' +
            logit_line + '\n' +
            time_line + '\n' +
            output_line + '\n'
        )

        # Move back to the start of the previous snapshot and clear it
        move_up = ""
        if self.line_count > 0:
            move_up = f"\033[{self.line_count}F\033[J"
        print(move_up + whole_line, flush=True)

        self.line_count = whole_line.count("\n") + 1

    def set_print_logit(self, logit: str) -> None:
        """
        """
        self.logit = repr(logit)

    def set_print_top(self, logits: dict[str, float]) -> None:
        """
        """
        self.logits = logits

    def set_print_pending(self, part: str) -> None:
        """Set the value being generated, replaced on every step until it
        is appended to the output.

        Args:
            part (str): decoded value generated so far

        Returns:
            None
        """
        self.pending = part

    def append_print_output(self, part: str, gen_mode: GenMode) -> None:
        """
        """
        if gen_mode == "generated":
            self.output = (
                self.output +
                COLOR.GREEN.value + part + COLOR.RESET.value
            )
        elif gen_mode == "autoCompleted":
            self.output = (
                self.output +
                COLOR.CYAN.value + part + COLOR.RESET.value
            )
        else:
            self.output = (
                self.output + part
            )

    @staticmethod
    def print_wait(u_input: str, enable: bool) -> None:
        """
        """
        if not enable:
            print(
                COLOR.YELLOW.value + '-> ' + COLOR.RESET.value +
                'Handling "' + COLOR.CYAN.value +
                u_input + COLOR.RESET.value + '". Please be patient...'
            )

    @staticmethod
    def print_done(u_input: str, enable: bool) -> None:
        """
        """
        if not enable:
            move_up = "\033[1F\033[J"
            print(
                move_up +
                COLOR.YELLOW.value + '-> ' + COLOR.RESET.value +
                'Handling "' + COLOR.CYAN.value +
                u_input + COLOR.RESET.value + '". ' +
                COLOR.GREEN.value + 'DONE.' +
                COLOR.RESET.value,
                flush=True
            )
