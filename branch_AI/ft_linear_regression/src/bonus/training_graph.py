from colorama import Fore
import matplotlib.pyplot as graph

def training_graph(theta0_list, theta1_list, save_path):
    print(f"{Fore.BLUE}Generating graph 2...{Fore.RESET}")
    step = []
    for i in range(len(theta0_list)):
        step.append(i)
    
    graph.plot(step, theta0_list, 'ro-', label='Theta0')
    graph.plot(step, theta1_list, 'bs-', label='Theta1')
    graph.title("Evolution of θ₀ and θ₁")
    graph.xlabel("Step")
    graph.ylabel("Value")
    graph.legend()
    graph.grid(True)
    graph.savefig(save_path)
    graph.show()
    print(f"{Fore.GREEN}Graph 2 exported!{Fore.RESET}")
    print()