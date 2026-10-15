from .a1_arg_parser import ArgParser
from .a2_file_handler import FileHandler
import sys


def main() -> None:
    arguments = ArgParser().parse(sys.argv[1:])
    functions = FileHandler().read_functions(arguments.functions)

    print(functions)


if __name__ == "__main__":
    main()
