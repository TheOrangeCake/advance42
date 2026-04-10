from trainer.main import training_program
from predictor.main import prediction_program
from bonus.main import bonus_program

data_file = "./trainer/data.csv"
export_file = "./trainer/thetas.csv"
save_fig = "./bonus/graph.jpg"

training_program(data_file, export_file)
prediction_program(export_file)
bonus_program(data_file, export_file, save_fig)