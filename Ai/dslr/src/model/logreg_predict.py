
from logreg_train import sigmoid
import sys
import logging
from helper.read_data import import_data
from prepare_data import (
    save_students_predictions,
    select_training_data_features,
    normalize_features_predict,
    read_weights,
    get_courses_list,
)
import numpy as np
from pathlib import Path


def logreg_predict(dataset_path, weights_path="weights.csv"):
    # uses the final values of wight and bias to compute the final
    # model's output for each training exemple and return
    # retunr predicted cprobability each training exemple"""

    weights_missing = not Path(weights_path).exists()

    if weights_missing:
        logging.critical(
            'Missing file containing the weights trained logreg_train'
            ' — run "make train" first'
        )
        sys.exit(1)

    try:
        (
            list_houses,
            list_courses,
            houses_biais,
            houses_weights,
            means,
            stds,
        ) = read_weights(weights_path)

    except (KeyError, ValueError) as e:
        logging.critical(e)
        sys.exit(1)

    # prepare datas:
    grades_data = get_grades_data(list_courses, means, stds)
    nb_stud = grades_data.shape[0]

    students_predictions = []
    for student in range(nb_stud):
        student_grade = grades_data[student]
        house_prob = np.zeros(len(list_houses))
        for house_index in range(len(list_houses)):
            probability = predict(
                student_grade,
                houses_weights[house_index],
                houses_biais[house_index],
            )
            house_prob[house_index] = probability
        students_predictions.append(
            most_probable_house(house_prob, list_houses)
        )
    try:
        save_students_predictions("houses.csv", students_predictions)
    except IOError as e:
        logging.critical(e)
        sys.exit(1)
    return


def get_grades_data(list_courses, means, stds) -> np.ndarray:
    prediction_data = import_data(sys.argv[1])
    try:
        test_courses = get_courses_list(prediction_data)
    except (KeyError, ValueError) as e:
        logging.critical(e)
        sys.exit(1)
    if test_courses != list_courses:
        raise ValueError(
            f"Course mismatch between test dataset and trained model.\n"
            f"Trained on: {list_courses}\nTest data has: {test_courses}"
        )
    students_grades = select_training_data_features(prediction_data)
    students_grades = normalize_features_predict(students_grades, means, stds)
    return students_grades


def most_probable_house(house_prob, list_house):
    return list_house[np.argmax(house_prob)]


def predict(
    student_grades: np.array, house_weights: np.array, house_biais: float
) -> float:
    z = np.dot(house_weights, student_grades) + house_biais
    probability = sigmoid(z)
    return float(probability)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        logging.critical('Usage: ./logreg_predict [Dataset path]')
        sys.exit(1)

    logging.info(f"Logreg Predict Dataset source: {sys.argv[1]}")
    logreg_predict(sys.argv[1])
