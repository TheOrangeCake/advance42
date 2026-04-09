from colorama import Fore
from prompt_input import prompt_input
from get_price import get_price

mileage = prompt_input()
price = get_price(mileage)

print("A car with", f"{Fore.YELLOW}{mileage}{Fore.RESET}", "kilometer(s) is estimated at", f"{Fore.CYAN}{price}{Fore.RESET}", "dollar(s)")