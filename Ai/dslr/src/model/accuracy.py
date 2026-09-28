from sklearn.metrics import accuracy_score
from logreg_train import logreg_train
from logreg_predict import logreg_predict
from helper.read_data import read_dataset
import sys
import logging


def test_accuracy(dataset_train_path, dataset_test_path,
                  iterations, learning_rate, batch_size):

    logreg_train(dataset_train_path, iterations, learning_rate, batch_size)
    logreg_predict(dataset_test_path)

    predictions_data = read_dataset("houses.csv")
    y_pred = predictions_data["Hogwarts House"]

    data_train = read_dataset(dataset_train_path)
    y_true = data_train["Hogwarts House"]

    try:
        final_accuracy_score = accuracy_score(y_true, y_pred)
    except ValueError:
        logging.critical("Input variables with inconsistent "
                         "numbers of samples")
        sys.exit(1)
    print("accuracy sklearn: ", final_accuracy_score)


if __name__ == "__main__":

    if len(sys.argv) < 3:
        logging.critical('Usage: ./accuracy [Dataset path] [Datatest path]')
        sys.exit(1)

    logging.info(f"Accuracy Dataset source: {sys.argv[1]}")

    try:
        if len(sys.argv) >= 4:
            epoch = int(sys.argv[3])
        else:
            epoch = 1000
        if epoch < 1:
            raise ValueError

        if len(sys.argv) >= 5:
            learning_rate = float(sys.argv[4])
        else:
            learning_rate = 0.01
        if learning_rate <= 0:
            raise ValueError

        if len(sys.argv) >= 6:
            batch_size = int(sys.argv[5])
        else:
            batch_size = None
        if batch_size is not None and batch_size < 1:
            raise ValueError
    except ValueError:
        logging.error(
            'Usage: make accuracy '
            'ARGS="<epochs:int> <learning_rate:float> <batch_size:int>"'
        )
        sys.exit(1)
    test_accuracy(sys.argv[1], sys.argv[2], epoch, learning_rate, batch_size)
