from colorama import Fore

def get_price(mileage):
    return mileage

if __name__ == "__main__":
    print("Enter mileage (No validation):")
    mileage = input()
    price = get_price(mileage)
    print("Price:", f"{Fore.GREEN}{price}{Fore.RESET}")    