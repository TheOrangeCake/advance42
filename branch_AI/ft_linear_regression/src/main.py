from trainer.main import training_program
from predictor.main import prediction_program

data_file = "./trainer/data.csv"
export_file = "./trainer/thetas.csv"

training_program(data_file, export_file)
prediction_program(export_file)