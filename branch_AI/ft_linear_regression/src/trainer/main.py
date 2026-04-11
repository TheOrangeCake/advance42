from colorama import Fore
try:
    from .import_data import import_data
    from .linear_regression import linear_regression
    from .export_thetas import export_thetas
    from .export_thetas_list import export_thetas_list
    from .export_cost import export_cost
except ImportError:
    from import_data import import_data
    from linear_regression import linear_regression
    from export_thetas import export_thetas
    from export_thetas_list import export_thetas_list
    from export_cost import export_cost

def training_program(data_file, export_thetas_file, export_thetas_list_file, export_cost_list_file):
    learning_rate = 1.7
    iterations = 100
    theta0 = 0.0
    theta1 = 0.0

    print()
    print("---------------------------------------------------------")
    print(f"{Fore.CYAN}Training program started{Fore.RESET}")
    print()

    data_list = import_data(data_file)
    trained_theta0, trained_theta1, km_min, km_max, price_min, price_max, thetas_list, cost_list = linear_regression(
        learning_rate,
        iterations,
        theta0,
        theta1,
        data_list)
    export_thetas(trained_theta0, trained_theta1, km_min, km_max, price_min, price_max, export_thetas_file)
    export_thetas_list(thetas_list, export_thetas_list_file)
    export_cost(cost_list, export_cost_list_file)

    print(f"{Fore.GREEN}All done! Training program terminated{Fore.RESET}")
    print()
    print("---------------------------------------------------------")
    print()

if __name__ == "__main__":
    data_file = "data.csv"
    export_thetas_file = "thetas.csv"
    export_thetas_list_file = "thetas_list.csv"
    export_cost_list_file = "cost.csv"
    training_program(data_file, export_thetas_file, export_thetas_list_file, export_cost_list_file)