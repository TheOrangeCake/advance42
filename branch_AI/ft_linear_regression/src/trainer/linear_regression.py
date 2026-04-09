from colorama import Fore

def linear_regression(learning_rate, iterations, theta0, theta1, data_list):
    print(f"{Fore.CYAN}Training model...{Fore.RESET}")
    data_list = normalize(data_list)
    m = len(data_list)
    for iteration in range(iterations):
        error_sum_theta0 = 0
        error_sum_theta1 = 0
        for row in data_list:
            estimate_price = estimate(theta0, theta1, row["km"])
            error = estimate_price - row["price"]
            error_sum_theta0 += error
            error_sum_theta1 += error * row["km"]
        theta0 -= learning_rate * error_sum_theta0 / m
        theta1 -= learning_rate * error_sum_theta1 / m
        if iteration % 100 == 0:
            cost = mean_squared_error(theta0, theta1, data_list)
            print(f"Iteration {iteration}: {Fore.YELLOW}Cost = {cost:.2f}{Fore.RESET}, {Fore.CYAN}θ₀ = {theta0:.4f}{Fore.RESET}, {Fore.BLUE}θ₁ = {theta1:.4f}{Fore.RESET}")
    print(f"{Fore.GREEN}Trained:{Fore.RESET} {Fore.CYAN}θ₀: {theta0:.4f}{Fore.RESET}, {Fore.BLUE}θ₁: {theta1:.4f}{Fore.RESET}")
    print()
    return theta0, theta1

def estimate(theta0, theta1, mileage):
    return theta0 + (theta1 * mileage)

def normalize(data_list):
    km_list = []
    price_list = []
    for row in data_list:
        km_list.append(row["km"])
        price_list.append(row["price"])

    km_min = min(km_list)
    km_max = max(km_list)
    price_min = min(price_list)
    price_max = max(price_list)

    for row in data_list:
        row["km"] = (row["km"] - km_min) / (km_max - km_min)
        row["price"] = (row["price"] - price_min) / (price_max - price_min)
    return data_list
    
def mean_squared_error(theta0, theta1, data_list):
    m = len(data_list)
    total_error = 0
    for row in data_list:
        estimate_price = estimate(theta0, theta1, row["km"])
        error = estimate_price - row["price"]
        total_error += error ** 2
    return total_error / m

def mock_dataset():
    km_list = [
        240000, 139800, 150500, 185530, 176000, 114800, 166800, 89000,
        144500, 84000, 82029, 63060, 74000, 97500, 67000, 76025, 48235,
        93000, 60949, 65674, 54000, 68500, 22899, 61789
    ]
    price_list = [
        3650, 3800, 4400, 4450, 5250, 5350, 5800, 5990, 5999, 6200, 6390,
        6390, 6600, 6800, 6800, 6900, 6900, 6990, 7490, 7555, 7990, 7990,
        7990, 8290
    ]
    data_list = []
    for i in range(len(km_list)):
        data_list.append({"km": km_list[i], "price": price_list[i]})
    return data_list

if __name__ == "__main__":
    learning_rate = 0.1
    iterations = 2201
    theta0 = 0.0
    theta1 = 0.0
    data_list = mock_dataset()
    trained_theta0, trained_theta1 = linear_regression(
        learning_rate,
        iterations,
        theta0,
        theta1,
        data_list)    

