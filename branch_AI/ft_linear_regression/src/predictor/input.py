from colorama import Fore, Style

def prompt_input():
    y = True
    while y == True:
        print("Please enter car mileage:")
        input_mileage = input()
        try:
            mileage = int(input_mileage)
            if mileage < 0:
                print(Fore.RED + "Error: " + Style.RESET_ALL + "Mileage must not be negative")
                print("------------------------------------------------------")
                print()
                continue
            y = False
        except:
            print(Fore.RED + "Error: " + Style.RESET_ALL + "Mileage must be a whole number")
            print("------------------------------------------------------")
            print()
    return mileage

if __name__ == "__main__":
    mileage = prompt_input()
    print(Fore.GREEN + "Success:" + Style.RESET_ALL, "User entered", f"{Fore.GREEN}{mileage}{Style.RESET_ALL}")
