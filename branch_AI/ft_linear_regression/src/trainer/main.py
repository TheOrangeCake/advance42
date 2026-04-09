from colorama import Fore
from .import_data import import_data
from .linear_regression import linear_regression
from .export_thetas import export_thetas

def training_program(file):
    learning_rate = 0.1
    iterations = 2201
    theta0 = 0.0
    theta1 = 0.0

    print()
    print("---------------------------------------------------------")
    print(f"{Fore.CYAN}Training program started{Fore.RESET}")
    print()

    data_list = import_data(file)
    trained_theta0, trained_theta1 = linear_regression(
        learning_rate,
        iterations,
        theta0,
        theta1,
        data_list)
    export_thetas(trained_theta0, trained_theta1)

    print(f"{Fore.GREEN}All done! Training program terminated{Fore.RESET}")
    print()
    print("---------------------------------------------------------")
    print()

if __name__ == "__main__":
    file = "data.csv"
    training_program(file)