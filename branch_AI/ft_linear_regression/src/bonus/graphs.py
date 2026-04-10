from colorama import Fore
import sys
import os
import matplotlib.pyplot as graph

sys.path.append(os.path.join(os.path.dirname(__file__), ".."))
from trainer.import_data import import_data
from predictor.get_thetas import get_thetas

def draw_graph(data_list, theta0, theta1, km_min, km_max, price_min, price_max, save_path):
    print(f"{Fore.BLUE}Generating graph...{Fore.RESET}")
    km_list = []
    price_list = []
    for row in data_list:
        km_list.append(row["km"])
        price_list.append(row["price"])

    x1 = min(km_list)
    x2 = max(km_list)
    y1 = (theta0 + theta1 * ((x1 - km_min) / (km_max - km_min))) * (price_max - price_min) + price_min
    y2 = (theta0 + theta1 * ((x2 - km_min) / (km_max - km_min))) * (price_max - price_min) + price_min

    graph.plot([x1, x2], [y1, y2], color="red")

    graph.scatter(km_list, price_list, color="blue")

    graph.title("Car price prediction based on mileage")
    graph.xlabel("Mileage (km)")
    graph.ylabel("Price (CHF)")
    graph.savefig(save_path)
    # graph.show()
    print(f"{Fore.GREEN}Graph exported!{Fore.RESET}")
    print()

if __name__ == "__main__":
    data_file = "../trainer/data.csv"
    export_file = "../trainer/thetas.csv"
    save_fig = "graph.jpg"
    data_list = import_data(data_file)
    theta0, theta1, km_min, km_max, price_min, price_max = get_thetas(export_file)
    draw_graph(data_list, theta0, theta1, km_min, km_max, price_min, price_max, save_fig)
