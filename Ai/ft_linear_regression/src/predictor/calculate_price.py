from colorama import Fore

def calculate_price(mileage, theta0, theta1, km_min, km_max, price_min, price_max):
    normalized_km = (mileage - km_min) / (km_max - km_min)
    normalized_price = theta0 + (theta1 * normalized_km)
    price = int(denormalize(normalized_price, price_min, price_max))
    return price if price >= 0 else 0

def denormalize(normalized_price, price_min, price_max):
    return normalized_price * (price_max - price_min) + price_min

if __name__ == "__main__":
    print("Enter mileage (No validation):")
    mileage = input()
    theta0 = 0.9393110496721372
    theta1 = -1.0035549436167959
    price = get_price(mileage) 
    print("Price:", f"{Fore.GREEN}{price}{Fore.RESET}")