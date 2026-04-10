from trainer.main import training_program
from predictor.main import prediction_program
from bonus.main import bonus_program

data_file = "./trainer/data.csv"
export_file = "./trainer/thetas.csv"
save_fig_1 = "./bonus/graph_1.jpg"
save_fig_2 = "./bonus/graph_2.jpg"

training_program(data_file, export_file)
bonus_program(data_file, export_file, save_fig_1, save_fig_2)
prediction_program(export_file)