from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler
from .z1_utils import error
import sys


def main() -> None:
    """Main program flow."""

    arguments = ArgParser().parse(sys.argv[1:])

    functions = FileHandler().read_functions(arguments.functions)
    if not functions:
        error("Empty function definition list")

    print(functions)


if __name__ == "__main__":
    main()
