from trainer.main import training_program
from predictor.main import prediction_program

data_file = "./trainer/data.csv"
training_program(data_file)
prediction_program()