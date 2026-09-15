from colorama import Fore

def r_squared(data_list, theta0, theta1, km_min, km_max, price_min, price_max):
    print(f"{Fore.BLUE}Calculating R²...{Fore.RESET}")
    prices = []
    for row in data_list:
        prices.append(row["price"])
    avg_price = sum(prices) / len(prices)

    ss_tot = 0
    for price in prices:
        ss_tot += (price - avg_price) ** 2

    ss_res = 0
    for row in data_list:
        ss_res += (row["price"] - ((theta0 + theta1 * ((row["km"] - km_min) / (km_max - km_min))) * (price_max - price_min) + price_min)) ** 2

    r2 = 1 - (ss_res / ss_tot)
    print(f"R²: {Fore.GREEN}{r2:.4f}{Fore.RESET} ({r2 * 100:.2f}%)")
    print()
