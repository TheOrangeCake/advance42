from colorama import Fore
import csv

def import_data(file_path, chunk_size = 500):
    dataDict = {}
    try:
        with open(file_path, mode = "r") as file:

            pass
    except csv.Error as e:
        print(f"{Fore.RED}Bad csv file:{Fore.RESET}: {e}")
    except Exception as e:
        print(f"{Fore.RED}Fail to open file:{Fore.RESET} {e}")


if __name__ == "__main__":
    print("Enter dataset csv file:")
    file_path = input()
    import_data(file_path)
