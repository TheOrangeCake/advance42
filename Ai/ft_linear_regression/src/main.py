from trainer.main import training_program
from predictor.main import prediction_program
from bonus.main import bonus_program

data_file = "./trainer/data.csv"
export_thetas_file = "./trainer/thetas.csv"
export_thetas_list_file = "./trainer/thetas_list.csv"
export_cost_file = "./trainer/cost.csv"
save_fig_1 = "./bonus/graph_1.jpg"
save_fig_2 = "./bonus/graph_2.jpg"
save_fig_3 = "./bonus/graph_3.jpg"

training_program(data_file, export_thetas_file, export_thetas_list_file, export_cost_file)
bonus_program(data_file, export_thetas_file, export_thetas_list_file, export_cost_file, save_fig_1, save_fig_2, save_fig_3)
prediction_program(export_thetas_file)