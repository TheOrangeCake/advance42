from colorama import Fore
try:
    from .import_data import import_data
    from .linear_regression import linear_regression
    from .export_thetas import export_thetas
except ImportError:
    from import_data import import_data
    from linear_regression import linear_regression
    from export_thetas import export_thetas

def training_program(data_file, export_file):
    learning_rate = 0.1
    iterations = 2201
    theta0 = 0.0
    theta1 = 0.0

    print()
    print("---------------------------------------------------------")
    print(f"{Fore.CYAN}Training program started{Fore.RESET}")
    print()

    data_list = import_data(data_file)
    trained_theta0, trained_theta1, km_min, km_max, price_min, price_max = linear_regression(
        learning_rate,
        iterations,
        theta0,
        theta1,
        data_list)
    export_thetas(trained_theta0, trained_theta1, km_min, km_max, price_min, price_max, export_file)

    print(f"{Fore.GREEN}All done! Training program terminated{Fore.RESET}")
    print()
    print("---------------------------------------------------------")
    print()

if __name__ == "__main__":
    data_file = "data.csv"
    export_file = "thetas.csv"
    training_program(data_file, export_file)