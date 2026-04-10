from colorama import Fore
import sys
import os

sys.path.append(os.path.join(os.path.dirname(__file__), ".."))
try:
    from .graphs import draw_graph
except ImportError:
    from graphs import draw_graph
from trainer.import_data import import_data
from predictor.get_thetas import get_thetas

def bonus_program(data_file, export_file, save_fig):
    print()
    print("---------------------------------------------------------")
    print(f"{Fore.CYAN}Bonus program started{Fore.RESET}")
    print()

    data_list = import_data(data_file)
    theta0, theta1, km_min, km_max, price_min, price_max = get_thetas(export_file)
    draw_graph(data_list, theta0, theta1, km_min, km_max, price_min, price_max, save_fig)

    print(f"{Fore.GREEN}Bonus done!{Fore.RESET}")
    print("---------------------------------------------------------")
    print()

if __name__ == "__main__":
    data_file = "../trainer/data.csv"
    export_file = "../trainer/thetas.csv"
    save_fig = "graph.jpg"
    bonus_program(data_file, export_file, save_fig)