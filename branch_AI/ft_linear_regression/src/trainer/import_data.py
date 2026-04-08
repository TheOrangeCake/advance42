from colorama import Fore
import csv
import sys
import re

def import_data(file_path, chunk_size = 5):
    try:
        with open(file_path, mode = "r") as file:
            if not is_headers_good(file):
                raise csv.Error("Bad headers")
            file.seek(0)

            data_list = []
            reader = csv.DictReader(file)
            for row in reader:
                try:
                    for key, value in row.items():
                        row[key] = is_values_good(value)
                except ValueError:
                    print(f"Bad data: line {reader.line_num}, ignored")
                    continue
                data_list.append(row)
                if len(data_list) == chunk_size:
                    yield data_list
                    data_list = []
            if data_list:
                yield data_list
    except csv.Error as e:
        sys.exit(f"{Fore.RED}Bad csv file:{Fore.RESET} {e}")
    except Exception as e:
        sys.exit(f"{Fore.RED}Fail to open file:{Fore.RESET} {e}")

def is_headers_good(file):
    first_line = next(file).strip()
    headers = re.split("\\s*,\\s*", first_line)
    return len(headers) == 2 and headers[0] == "km" and headers[1] == "price"

def is_values_good(value):
    num = int(value)
    if num < 0:
        raise ValueError()
    return num

if __name__ == "__main__":
    print("Enter dataset csv file:")
    file_path = input()
    for data_list in import_data(file_path):
        for row in data_list:
            print(row)
