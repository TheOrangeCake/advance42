from colorama import Fore
import sys
import os

sys.path.append(os.path.join(os.path.dirname(__file__), ".."))
try:
    from .regression_graph import regression_graph
    from .training_graph import training_graph
    from .get_thetas_history import get_thetas_history
except ImportError:
    from regression_graph import regression_graph
    from training_graph import training_graph
    from get_thetas_history import get_thetas_history
from trainer.import_data import import_data
from predictor.get_thetas import get_thetas

def bonus_program(data_file, export_thetas_file, export_thetas_list_file, save_fig_1, save_fig_2):
    print()
    print("---------------------------------------------------------")
    print(f"{Fore.CYAN}Bonus program started{Fore.RESET}")
    print()

    data_list = import_data(data_file)
    theta0, theta1, km_min, km_max, price_min, price_max = get_thetas(export_thetas_file)
    regression_graph(data_list, theta0, theta1, km_min, km_max, price_min, price_max, save_fig_1)
    
    theta0_list, theta1_list = get_thetas_history(export_thetas_list_file)
    training_graph(theta0_list, theta1_list, save_fig_2)

    print(f"{Fore.GREEN}Bonus done!{Fore.RESET}")
    print("---------------------------------------------------------")
    print()

if __name__ == "__main__":
    data_file = "../trainer/data.csv"
    export_thetas_file = "../trainer/thetas.csv"
    export_thetas_list_file = "../trainer/thetas_list.csv"
    save_fig_1 = "graph_1.jpg"
    save_fig_2 = "graph_2.jpg"
    bonus_program(data_file, export_thetas_file, export_thetas_list_file, save_fig_1, save_fig_2)