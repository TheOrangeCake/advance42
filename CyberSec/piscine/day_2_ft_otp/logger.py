from colorama import Fore

def error(message: str) -> None:
    print(f"[{Fore.RED}ERROR{Fore.RESET}]", message)

def info(message: str) -> None:
    print(f"[{Fore.YELLOW}INFO{Fore.RESET}]", message)

def success(message: str) -> None:
    print(f"[{Fore.GREEN}SUCCESS{Fore.RESET}]", message)