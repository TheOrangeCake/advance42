from colorama import Fore
import sys
import os

def export_thetas_list(thetas_list, export_file):
    print(f"{Fore.CYAN}Exporting θ₀ and θ₁ training history...{Fore.RESET}")

    try:
        with open(export_file, mode = "w") as file:
            file.write("theta0,theta1\n")
            for row in thetas_list:
                file.write(f"{str(row["theta0"])},{str(row["theta1"])}\n")
    except Exception as e:
        os.remove(export_file)
        sys.exit(f"{Fore.RED}Fail to export {export_file}:{Fore.RESET} {e}")
    print(f"{Fore.GREEN}Thetas training history exported!{Fore.RESET}")
    print()
