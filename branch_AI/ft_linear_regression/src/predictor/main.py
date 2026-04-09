from colorama import Fore
from .prompt_input import prompt_input
from .get_price import get_price
from .get_thetas import get_thetas

def prediction_program():
    mileage = prompt_input()
    theta0, theta1 = get_thetas()
    price = get_price(mileage, theta0, theta1)

    print("A car with", f"{Fore.YELLOW}{mileage}{Fore.RESET}", "kilometer(s) is estimated at", f"{Fore.CYAN}{price}{Fore.RESET}", "dollar(s)")

if __name__ == "__main__":
    prediction_program()