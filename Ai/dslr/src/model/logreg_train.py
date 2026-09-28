#!/usr/bin/env python3
import matplotlib.pyplot as graph
import sys
import logging
from pathlib import Path
import coloredlogs
sys.path.append(str(Path(__file__).parent.parent))
from helper.read_data import (  # noqa: E402
    import_data,
    get_houses_list,
    get_courses_list,
)

from helper.plot import save_fig  # noqa: E402
coloredlogs.install()

import numpy as np  # noqa: E402

from prepare_data import (  # noqa: E402
    get_binary_labels_for_house,
    init_weights_file,
    save_house_weights,
    select_training_data_features,
    normalize_features,
    save_normalization,
)


COLORS = {
    "Gryffindor": "red",
    "Ravenclaw":  "blue",
    "Hufflepuff": "yellow",
    "Slytherin":  "green",
}

# weight = valeur apprise par le modèle qui indique l’importance et
# l’influence d’une feature sur la prédiction.
# biais = valeur apprise par le modèle .2qui décale la prédiction de
# base, indépendamment des features.
# weight et biais c'est ce que le modèle apprends


def logreg_train(dataset_path, epoch, learning_rate, batch_size):
    print("epochs:", epoch, "learning_rate:", learning_rate)

    # prepare datas
    data = import_data(dataset_path)
    try:
        list_houses: list[str] = get_houses_list(data)
        list_courses: list[str] = get_courses_list(data)
    except (KeyError, ValueError) as e:
        logging.critical(e)
        sys.exit(1)

    # grades for each student
    training_data_features = select_training_data_features(data)

    training_data_features, means, std = normalize_features(
        training_data_features
    )
    for i in range(len(training_data_features)):
        np.set_printoptions(suppress=True, precision=3, linewidth=200)
        # print(training_data_features[i])

    # result values to train on
    houses_labels = data["Hogwarts House"]
    # print(houses_labels)

    # check if same numbers of students and result values
    if len(training_data_features) != len(houses_labels):
        return
    global nb_training_exmpl
    global nb_input_features

    # nb lines = nb students to train on
    nb_training_exmpl = len(training_data_features)  # nb de lignes
    # nb rows = nb branches/features to train on
    nb_input_features = training_data_features.shape[1]  # nb de colonnes

    # train the model
    # Create history variable to store cost during training
    history = {}
    house_weights = []

    # one-vs-Rest. Train on each house
    for house in list_houses:
        # the selected house is 1, other houses are 0
        training_data_labels = get_binary_labels_for_house(
            houses_labels, house
        )
        # the logistic regression function
        final_weight, final_biais, cost_history = logistic_regression(
            training_data_features,
            training_data_labels,
            learning_rate,
            epoch, batch_size
        )
        house_weights.append([house, final_biais, *final_weight])
        history[house] = cost_history

    # store data
    try:
        init_weights_file("weights.csv", list_courses)
        save_house_weights("weights.csv", house_weights)
        save_normalization("weights.csv", means, std)

    except IOError as e:
        logging.critical(e)
        sys.exit(1)

    draw_history(history, epoch, learning_rate, list_houses)
    return


def logistic_regression(
    training_data_features,
    training_data_labels,
    learning_rate,
    epoch,
    batch_size
):
    # uses the gradient function to update wheight and bias over a
    # specified number of epoch
    nb_training_data_features = len(training_data_features)
    if batch_size is None:
        batch_size = nb_training_data_features

    weight = np.zeros(nb_input_features)
    biais = 0
    cost_history = []

    for i in range(epoch):
        order = np.random.permutation(nb_training_data_features)
        for start in range(0, nb_training_data_features, batch_size):
            idx = order[start:start + batch_size]
            grad_biais, grad_weight = gradient_function(
                training_data_features[idx],
                training_data_labels[idx],
                weight,
                biais
            )

            weight = weight - learning_rate * grad_weight
            biais = biais - learning_rate * grad_biais

        current_cost = cost_function(
            training_data_features, training_data_labels, weight, biais
        )
        if i % 100 == 0:
            print(f"EPOCH {i}: Cost {current_cost}")
        cost_history.append(current_cost)
    return weight, biais, cost_history


def gradient_function(
    training_data_features, training_data_labels, weight, biais
):
    # compute derivative of the cost function with respect weight and biais
    nb_examples = len(training_data_features)
    # init grad w as a vector
    grad_weight = np.zeros(nb_input_features)
    grad_biais = 0

    for i in range(nb_examples):
        z = np.dot(weight, training_data_features[i]) + biais
        g = sigmoid(z)

        # difference between the output of our model and the true laber Y at I
        grad_biais += (g - training_data_labels[i])
        for j in range(nb_input_features):
            grad_weight[j] += (
                (g - training_data_labels[i]) * training_data_features[i, j]
            )

    grad_biais = (1/nb_examples) * grad_biais
    grad_weight = (1/nb_examples) * grad_weight

    return grad_biais, grad_weight
    # gradient descent


def cost_function(
    training_data_features, training_data_labels: np.ndarray, weight, biais
):
    # how well how model is doing

    # accumulate the total error across all the training examples
    cost_sum = 0

    # loop over each training exemple
    for i in range(nb_training_exmpl):
        # z = linear combinaton input
        z = np.dot(weight, training_data_features[i]) + biais
        g = sigmoid(z)

        cost_sum += (
            - training_data_labels[i] * np.log(g)
            - (1 - training_data_labels[i]) * np.log(1 - g)
        )
        # return the average cost
    return (1/nb_training_exmpl) * cost_sum


# sigmoid/ logistic function
def sigmoid(z) -> float:
    # return float between 0.0 and 1.0 probability
    return 1 / (1 + np.exp(-z))


def draw_history(
    history: dict[str, list[float]], epoch, learning_rate, list_houses
) -> None:
    for house in list_houses:
        graph.plot(history[house], label=house, color=COLORS[house])
    graph.title(
        f"Training history (step {learning_rate}, {epoch} epochs)"
    )
    graph.xlabel("Epoch")
    graph.ylabel("Cost J(θ)")
    graph.legend()
    graph.grid(alpha=0.3)
    save_fig("model", "training_history.png")
    # graph.show()


if __name__ == "__main__":
    if len(sys.argv) < 2:
        logging.error('Usage: ./logreg_train [Dataset path]')
        sys.exit(1)

    logging.info(f"Logreg Train Dataset source: {sys.argv[1]}")

    try:
        if len(sys.argv) >= 3:
            epoch = int(sys.argv[2])
        else:
            epoch = 1000
        if epoch < 1:
            raise ValueError

    # learning_rate = step size of gradient descents
        if len(sys.argv) >= 4:
            learning_rate = float(sys.argv[3])
        else:
            learning_rate = 0.01
        if learning_rate <= 0:
            raise ValueError

        if len(sys.argv) >= 5:
            batch_size = int(sys.argv[4])
        else:
            batch_size = None
        if batch_size is not None and batch_size < 1:
            raise ValueError

    except ValueError:
        logging.error(
            'Usage: ./logreg_train.py [Dataset path] '
            'ARGS="<epochs:int> <learning_rate:float> <batch_size:int>"'
        )
        sys.exit(1)

    logreg_train(sys.argv[1], epoch, learning_rate, batch_size)
