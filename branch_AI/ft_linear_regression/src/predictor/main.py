from colorama import Fore
try:
    from .prompt_input import prompt_input
    from .calculate_price import calculate_price
    from .get_thetas import get_thetas
except ImportError:
    from prompt_input import prompt_input
    from calculate_price import calculate_price
    from get_thetas import get_thetas

def prediction_program(export_file):
    print()
    print("---------------------------------------------------------")
    print(f"{Fore.CYAN}Predictor program started{Fore.RESET}")
    print()

    mileage = prompt_input()
    theta0, theta1, km_min, km_max, price_min, price_max = get_thetas(export_file)
    price = calculate_price(mileage, theta0, theta1, km_min, km_max, price_min, price_max)

    print("A car with", f"{Fore.YELLOW}{mileage}{Fore.RESET}", "kilometer(s) is estimated at", f"{Fore.CYAN}{price:.2f}{Fore.RESET}", "dollar(s)")
    print()
    print("---------------------------------------------------------")
    print()
if __name__ == "__main__":
    export_file = "../trainer/thetas.csv"
    prediction_program(export_file)