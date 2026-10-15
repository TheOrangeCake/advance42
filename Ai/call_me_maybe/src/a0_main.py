from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler
from .z1_utils import Utils
from pydantic import BaseModel
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
        fileHandler = FileHandler()
        functions = fileHandler.read_functions(arguments.functions)
        if not functions:
            Utils.error("Empty function definition list")

        # Read input prompts
        inputs = fileHandler.read_inputs(arguments.inputs)
        if not inputs:
            Utils.error("Empty input list")

        # Check output file validity
        fileHandler.validate_output_file(arguments.outputs)
