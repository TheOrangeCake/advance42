from colorama import Fore
import csv
import sys
import re

def import_data(file_path):
    print(f"{Fore.CYAN}Importing data...{Fore.RESET}")
    data_list = []
    try:
        with open(file_path, mode = "r") as file:
            if not is_headers_good(file):
                raise csv.Error("Bad headers: must be \"km,price\" exactly")
            file.seek(0)

            reader = csv.DictReader(file)
            for row in reader:
                try:
                    for key, value in row.items():
                        row[key] = is_values_good(value)
                except ValueError:
                    print(f"Bad data: line {reader.line_num}, ignored")
                    continue
                data_list.append(row)
            print(f"{Fore.GREEN}Data imported!{Fore.RESET}")
            print()
            return data_list
    except csv.Error as e:
        sys.exit(f"{Fore.RED}Bad csv file:{Fore.RESET} {e}")
    except Exception as e:
        sys.exit(f"{Fore.RED}Fail to open data file:{Fore.RESET} {e}")

def is_headers_good(file):
    first_line = next(file).rstrip('\n')
    return first_line == "km,price"

def is_values_good(value):
    num = int(value)
    if num < 0:
        raise ValueError()
    return num

if __name__ == "__main__":
    print("Enter dataset csv file:")
    file_path = input()
    for row in import_data(file_path):
        print(row)
