from colorama import Fore
import matplotlib.pyplot as graph

def cost_graph(iterations, cost_list, save_path):
    print(f"{Fore.BLUE}Generating graph 3...{Fore.RESET}")

    graph.figure()
    graph.plot(iterations, cost_list, 'r.', label='Error')
    graph.title("Evolution of error between prediction and actual data")
    graph.xlabel("Iteration")
    graph.ylabel("Value")
    graph.legend()
    graph.grid(True)
    graph.savefig(save_path)
    # graph.show(block=False)
    # graph.close()
    print(f"{Fore.GREEN}Graph 3 exported!{Fore.RESET}")
    print()
