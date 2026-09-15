from colorama import Fore
import sys
import os

def prompt_input():
    y = True
    while y == True:
        print("Please enter car mileage:")
        try:
            input_mileage = input()
            mileage = int(input_mileage)
            if mileage < 0:
                print(Fore.RED + "Error: " + Fore.RESET + "Mileage must not be negative")
                print("------------------------------------------------------")
                print()
                continue
            y = False
        except ValueError:
            print(Fore.RED + "Error: " + Fore.RESET + "Mileage must be a whole number")
            print("------------------------------------------------------")
            print()
        except KeyboardInterrupt:
            print(Fore.YELLOW + "\nOperation cancelled by user." + Fore.RESET)
            print("------------------------------------------------------")
            print()
            sys.exit()
        except EOFError:
            print(Fore.YELLOW + "\nInput terminated by user." + Fore.RESET)
            print("------------------------------------------------------")
            print()
            sys.exit()
    print()
    return mileage

if __name__ == "__main__":
    mileage = prompt_input()
    print(Fore.GREEN + "Success:" + Fore.RESET, "User entered", f"{Fore.GREEN}{mileage}{Fore.RESET}")
