import sys

from logger import *
from n_handler import handle as n_handler
from g_handler import handle as g_handler
from k_handler import handle as k_handler

def main():
    info("ft_otp started")

    if (len(sys.argv) < 3):
        error("Not enough argument. Format: -flag(n/g/k) argument")
        return
    if (len(sys.argv) > 3):
        error("Too many argument. Format: -flag(n/g/k) argument")
        return
    
    match sys.argv[1]:
        case "-n":
            n_handler(sys.argv[2])
        case "-g":
            g_handler(sys.argv[2])
        case "-k":
            k_handler(sys.argv[2])
        case _:
            error("Invalid flag. Format: -flag(n/g/k) argument")

if __name__ == "__main__":
    main()
