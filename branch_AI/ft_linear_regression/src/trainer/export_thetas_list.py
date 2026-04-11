from colorama import Fore
import sys
import os

def export_thetas_list(thetas_list, export_file):
    print(f"{Fore.CYAN}Exporting θ₀ and θ₁ training history...{Fore.RESET}")

    try:
        with open(export_file, mode = "w") as file:
            file.write("theta0,theta1\n")
            for row in thetas_list:
                file.write(f"{str(row["theta0"])},{str(row["theta1"])}\n")
    except Exception as e:
        os.remove(export_file)
        sys.exit(f"{Fore.RED}Fail to export {export_file}:{Fore.RESET} {e}")
    print(f"{Fore.GREEN}Thetas training history exported!{Fore.RESET}")
    print()

def mock_data():
    theta0_list = [
        0.0,
        0.05779813218390804, 0.10927102486437283, 0.15513627692845455,
        0.19602994475634383, 0.23251580874546174, 0.26509358677971007,
        0.29420621431384214, 0.32024629714353636, 0.34356183087781117,
        0.3644612704462963, 0.3832180235039033, 0.4000744332016423,
        0.41524530835241086, 0.42892105242614276, 0.44127043696365814,
        0.4524430598177495, 0.46257152403798096, 0.471773369145465,
        0.48015278293621183, 0.4878021187539539
    ]
    theta1_list = [
        0.0,
        0.015148619391263255, 0.02793437532808123, 0.03862896934606161,
        0.04747321080151025, 0.05468052756114942, 0.06044007773393967,
        0.06491950778289253, 0.06826739720252856, 0.07061542538090614,
        0.07208029221738418, 0.07276542047951033, 0.0727624647023718,
        0.07215264861507567, 0.07100795058067069, 0.06939215432138295,
        0.06736178023824893, 0.06496691089449326, 0.06225192268996711,
        0.05925613438716997, 0.056014381937907184
    ]
    thetas_list = []
    for i in range(len(theta0_list)):
        thetas_list.append({"theta0": theta0_list[i], "theta1": theta1_list[i]})
    return thetas_list

if __name__ == "__main__":
    export_thetas_list_file = "thetas_list.csv"
    mock_data = mock_data()
    export_thetas_list(mock_data, export_thetas_list_file)