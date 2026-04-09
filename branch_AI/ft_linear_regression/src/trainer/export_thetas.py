from colorama import Fore
import sys
import os

def export_thetas(theta0, theta1):
    print(f"{Fore.CYAN}Exporting θ₀ and θ₁...{Fore.RESET}")

    try:
        with open("thetas.csv", "w") as file:
            file.write("theta0,theta1\n")
            file.write(f"{str(theta0)},{str(theta1)}")
    except Exception as e:
        os.remove("thetas.csv")
        sys.exit(f"{Fore.RED}Fail to open export file:{Fore.RESET} {e}")

    print(f"{Fore.GREEN}Variables θ₀ and θ₁ exported!{Fore.RESET}")
    print()

if __name__ == "__main__":
    theta0 = 1.0
    theta1 = 2.0
    export_thetas(theta0, theta1)