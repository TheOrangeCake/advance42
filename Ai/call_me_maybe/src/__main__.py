from .a1_arg_parser import argParser
from .a2_file_handler import fileHandler
import sys


def main() -> None:
    arguments = argParser().parse(sys.argv[1:])
    functions = fileHandler().read_functions(arguments.functions)
    
    for function in functions:
        print(function['name'])
        print(function['description'])
        print(function['parameters'])
        print(function['returns'])
        print()


if __name__ == "__main__":
    main()
