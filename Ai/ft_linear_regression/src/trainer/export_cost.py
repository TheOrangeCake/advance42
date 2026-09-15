from colorama import Fore
import sys
import os

def export_cost(cost_list, export_file):
    print(f"{Fore.CYAN}Exporting cost evolution...{Fore.RESET}")

    try:
        with open(export_file, mode = "w") as file:
            file.write("iteration,cost\n")
            for row in cost_list:
                file.write(f"{str(row["iteration"])},{str(row["cost"])}\n")
    except Exception as e:
        os.remove(export_file)
        sys.exit(f"{Fore.RED}Fail to export {export_file}:{Fore.RESET} {e}")
    print(f"{Fore.GREEN}Cost evolution exported!{Fore.RESET}")
    print()
