from colorama import Fore, Style

def calculate_price(mileage):
    return mileage

if __name__ == "__main__":
    print("Enter mileage (No validation):")
    mileage = input()
    price = calculate_price(mileage)
    print("Price calculated:", f"{Fore.GREEN}{price}{Style.RESET_ALL}")    