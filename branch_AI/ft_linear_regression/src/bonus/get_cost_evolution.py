from colorama import Fore
import csv
import os
import sys

def get_cost_evolution(export_file):
    print(f"{Fore.CYAN}Getting cost evolution from {export_file}...{Fore.RESET}")
    try:
        with open(export_file, mode = "r") as file:
            if not is_headers_good(file):
                raise csv.Error(f"Bad {export_file} headers: must be \"iteration,cost\" exactly")
            file.seek(0)
            
            reader = csv.DictReader(file)
            iterations = []
            cost_list = []
            for row in reader:
                iterations.append(float(row["iteration"]))
                cost_list.append(float(row["cost"]))
            print(f"{Fore.GREEN}Training cost evolution read!{Fore.RESET}")
            print()
            return iterations, cost_list

    except csv.Error or ValueError as e:
        sys.exit(f"{Fore.RED}Bad {export_file} file:{Fore.RESET} {e}")
    except Exception as e:
        sys.exit(f"{Fore.RED}Fail to open {export_file} file:{Fore.RESET} {e}")

def is_headers_good(file):
    first_line = next(file).rstrip('\n')
    return first_line == "iteration,cost"

if __name__ == "__main__":
    export_file = "../trainer/cost.csv"
    theta0_list, theta1_list = get_cost_evolution(export_file)