from colorama import Fore

def get_price(mileage, theta0, theta1):
    return mileage

if __name__ == "__main__":
    print("Enter mileage (No validation):")
    mileage = input()
    theta0 = 0.9393110496721372
    theta1 = -1.0035549436167959
    price = get_price(mileage)
    print("Price:", f"{Fore.GREEN}{price}{Fore.RESET}")    