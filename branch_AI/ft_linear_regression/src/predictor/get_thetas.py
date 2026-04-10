from colorama import Fore
import csv
import sys

def get_thetas(export_file):
    print(f"{Fore.CYAN}Getting thetas from {export_file}...{Fore.RESET}")
    try:
        with open(export_file, mode = "r") as file:
            if not is_headers_good(file):
                raise csv.Error(f"Bad {export_file} headers: must be \"theta0,theta1,km_min,km_max,price_min,price_max\" exactly")
            file.seek(0)
            
            reader = csv.DictReader(file)
            row = next(reader)
            theta0 = float(row["theta0"])
            theta1 = float(row["theta1"])
            km_min = float(row["km_min"])
            km_max = float(row["km_max"])
            price_min = float(row["price_min"])
            price_max = float(row["price_max"])
            print(f"{Fore.GREEN}Thetas read!{Fore.RESET}")
            print()
            return theta0, theta1, km_min, km_max, price_min, price_max

    except csv.Error or ValueError as e:
        sys.exit(f"{Fore.RED}Bad {export_file} file:{Fore.RESET} {e}")
    except Exception as e:
        sys.exit(f"{Fore.RED}Fail to open {export_file} file:{Fore.RESET} {e}")

def is_headers_good(file):
    first_line = next(file).rstrip('\n')
    return first_line == "theta0,theta1,km_min,km_max,price_min,price_max"

if __name__ == "__main__":
    export_file = "../trainer/thetas.csv"
    theta0, theta1 = get_thetas(export_file)
    print(f"{Fore.GREEN}Thetas read:{Fore.RESET} {Fore.CYAN}θ₀: {theta0}{Fore.RESET}, {Fore.BLUE}θ₁: {theta1}{Fore.RESET}")
