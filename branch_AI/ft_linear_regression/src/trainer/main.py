from import_data import import_data
from linear_regression import linear_regression

file = "data.csv"
learning_rate = 0.1
iterations = 2201
theta0 = 0.0
theta1 = 0.0

data_list = import_data(file)
trained_theta0, trained_theta1 = linear_regression(
    learning_rate,
    iterations,
    theta0,
    theta1,
    data_list)
print(f"{Fore.GREEN}Trained:{Fore.RESET} {Fore.CYAN}θ₀: {trained_theta0:.4f}{Fore.RESET}, {Fore.BLUE}θ₁: {trained_theta1:.4f}{Fore.RESET}")
