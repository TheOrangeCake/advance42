from colorama import Fore
import csv
import os
import sys

def get_thetas_history(export_file):
    print(f"{Fore.CYAN}Getting thetas training history from {export_file}...{Fore.RESET}")
    try:
        with open(export_file, mode = "r") as file:
            if not is_headers_good(file):
                raise csv.Error(f"Bad {export_file} headers: must be \"theta0,theta1\" exactly")
            file.seek(0)
            
            reader = csv.DictReader(file)
            theta0_list = []
            theta1_list = []
            for row in reader:
                theta0_list.append(float(row["theta0"]))
                theta1_list.append(float(row["theta1"]))
            print(f"{Fore.GREEN}Thetas training history read!{Fore.RESET}")
            print()
            return theta0_list, theta1_list

    except csv.Error or ValueError as e:
        sys.exit(f"{Fore.RED}Bad {export_file} file:{Fore.RESET} {e}")
    except Exception as e:
        sys.exit(f"{Fore.RED}Fail to open {export_file} file:{Fore.RESET} {e}")

def is_headers_good(file):
    first_line = next(file).rstrip('\n')
    return first_line == "theta0,theta1"

if __name__ == "__main__":
    export_file = "../trainer/thetas_list.csv"
    theta0_list, theta1_list = get_thetas_history(export_file)