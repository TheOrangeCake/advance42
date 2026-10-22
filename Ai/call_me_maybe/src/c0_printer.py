from pydantic import BaseModel, ConfigDict
from enum import Enum
from datetime import datetime


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
    line_count: int = 1

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
            start_time = datetime.now()
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

        logit_line = (
            line_indent +
            COLOR.BLUE.value + 'Selected logit:\t' + COLOR.RESET.value +
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

        whole_line = (
            input_line + '\n' +
            logit_line + '\n' +
            time_line + '\n'
        )

        if self.line_count > 0:
            print(
                f"\033[{self.line_count}F\033[J" +
                whole_line,
                flush=True
            )
        else:
            return

        self.line_count = whole_line.count("\n") + 1
        return
    
    def set_print_logit(self, logit: str) -> None:
        """
        """
        self.logit = repr(logit)
        return
