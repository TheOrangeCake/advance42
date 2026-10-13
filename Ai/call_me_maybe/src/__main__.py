from .a1_arg_parse import argParser
from .a2_arg_validator import argValidator
import sys


def main() -> None:
    arguments = argParser().parse(sys.argv[1:])
    res = argValidator().validate(arguments)
    print(res)


if __name__ == "__main__":
    main()
