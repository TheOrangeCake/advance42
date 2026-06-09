from colorama import Fore
import sys

def main():
    print(f"{Fore.CYAN}ft_otp started.{Fore.RESET}")
    if (len(sys.argv) < 2):
        print(f"{Fore.RED}No argument provided.{Fore.RESET}", "Use \"-g\" to replace the hex key, or \"-k\" to generate a 6 digit TOTP key.")
        return
    for av in sys.argv[1:]:
        print(av)

if __name__ == "__main__":
    main()
