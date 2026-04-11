from colorama import Fore
import sys
import os

def export_thetas(theta0, theta1, km_min, km_max, price_min, price_max, export_file):
    print(f"{Fore.CYAN}Exporting θ₀ and θ₁...{Fore.RESET}")

    try:
        with open(export_file, mode = "w") as file:
            file.write("theta0,theta1,km_min,km_max,price_min,price_max\n")
            file.write(f"{str(theta0)},{str(theta1)},{str(km_min)},{str(km_max)},{str(price_min)},{str(price_max)}")
    except Exception as e:
        os.remove(export_file)
        sys.exit(f"{Fore.RED}Fail to export {export_file}:{Fore.RESET} {e}")

    print(f"{Fore.GREEN}Variables θ₀ and θ₁ exported!{Fore.RESET}")
    print()

if __name__ == "__main__":
    theta0 = 1.0
    theta1 = 2.0
    export_thetas(theta0, theta1, "thetas.csv")