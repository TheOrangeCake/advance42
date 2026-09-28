#!/usr/bin/env python3
import matplotlib.pyplot as graph
import sys
import logging
import math
import csv
from pathlib import Path
import coloredlogs
sys.path.append(str(Path(__file__).parent.parent))
from helper.read_data import read_dataset, is_numeric_column  # noqa: E402
from helper.plot import save_fig  # noqa: E402
from describe.min_max_perc import cal_min, cal_max  # noqa: E402

coloredlogs.install()

# Pseudo code:
#  1 - [OK] Define a learning rate / step so training can be faster
#  2 - [OK] Define number of epochs
#  3 - [OK] Define batch size (for GD, mini-batch and stochastic)
#  4 - [OK] Go through the dataset to get the min and max
#  5 - [OK] Normalize the data, if data is empty, put 0.5
#  6 - [OK] Convert column based data to row based data
#  7 - [OK] Initialize a set of weights (1 per feature) per house
#  8 - [OK] Create history variable to store cost during training
#  9 - [OK] Loop each epochs
# 10 - - [OK] Loop each house
# 11 - - - [OK] Loop each batch of students
# 12 - - - - [OK] Create fresh accumulator (Σ) for each batch
# 13 - - - - [OK] Loop each student in the batch
# 14 - - - - - [OK] Store the correct result: Same house 1, other house 0
# 15 - - - - - [OK] Calculate the probability of same house
# 16 - - - - - [OK] Calculate error = predict - truth
# 17 - - - - - [OK] Accumulate the accumulator
# 18 - - - - [OK] Update the weights using the batch average
# 19 - - - [OK] Save training data for graph
# 20 - [OK] Write weights, min and max to a file for classification later
# 21 - [OK] Draw training history graph to validate the training (θ converge)

HOUSES = ["Gryffindor", "Ravenclaw", "Hufflepuff", "Slytherin"]
SKIP = ["Index", "Hogwarts House", "First Name",
        "Last Name", "Birthday", "Best Hand"]
COLORS = {
    "Gryffindor": "red",
    "Ravenclaw":  "blue",
    "Hufflepuff": "yellow",
    "Slytherin":  "green",
}
WEIGHTS_PATH = Path(__file__).resolve().parents[2] / "weights.csv"

#  1 - [OK] Define a learning rate / step so training can be faster
#      GD: 0.5; mini-batch: 0.5; SGD: 0.5;
STEP = 2.0
#  2 - [OK] Define number of epochs
#      GD: 1000; mini-batch: 200; SDG: 75;
EPOCHS = 1000
#  3 - [OK] Define batch size
# important pour bonus to do sylvie
#      GD: None; mini-batch: 32; SGD: 1;
BATCH_SIZE = None


def train() -> None:
    if len(sys.argv) != 2:
        logging.critical('Usage: ./logreg_train [Dataset path]')
        return
    else:
        logging.info(f"Logreg_train Dataset source: {sys.argv[1]}")

    data = read_dataset(sys.argv[1])

    features = [name for name in data.keys()
                if name not in SKIP and is_numeric_column(data[name])]

    #  4 - [OK] Go through the dataset to get the min and max
    min_max = cal_min_max(features, data)
    #  5 - [OK] Normalize the data, if data is empty, put 0.5
    normalized = normalize(features, data, min_max)
    # 6 - [OK] Convert column based data to row based data
    rows = convert_to_rows(features, data, normalized)

    size = min(BATCH_SIZE or len(rows), len(rows))

    #  7 - [OK] Initialize a set of weights (1 per feature) per house
    house_w = {house: {name: 0.0 for name in features} for house in HOUSES}

    #  8 - [OK] Create history variable to store cost during training
    history = {house: [] for house in HOUSES}

    #  9 - [OK] Loop each epochs
    for i in range(EPOCHS):
        logging.info(f"Epoch {i}")
        # 10 - [OK] Loop each house
        for house in HOUSES:
            weights = house_w[house]
            # 11 - [OK] Loop each batch of students
            for batch in chunks(rows, size):
                # 12 - [OK] Create fresh accumulator (Σ) for each batch
                gradient = {feature: 0.0 for feature in features}
                # 13 - [OK] Loop each student in the batch
                for label, values in batch:
                    # 14 - [OK] Store the truth: Same house 1, other house 0
                    y = 1.0 if label == house else 0.0
                    # 15 - [OK] Calculate the probability of same house
                    p = hypothesis(weights, values)
                    # 16 - [OK] Calculate error = predict - truth
                    error = p - y
                    # 17 - [OK] Accumulate the accumulator
                    for feature in features:
                        gradient[feature] += error * values[feature]
                # 18 - [OK] Update the weights with this batch's accumulator
                # This is the actual Gradient Descent step, divide by the batch
                for feature in features:
                    weights[feature] -= STEP * (gradient[feature] / len(batch))
            # 19 - [OK] Save training data for graph (over the whole set)
            history[house].append(cost(rows, house, weights))

    # 20 - [OK] Write weights, min and max to a file for classification later
    save_weights(features, house_w, min_max)
    # 21 - [OK] Draw training history graph to validate the training
    draw_history(history)


# Store data logreg_predict needs to classify student
# Layout:
# Key       | Arithmancy | Astronomy | ...
# min       | -24370.0   | -966.74   | ...
# max       | 104956.0   | 1016.21   | ...
# Gryffindor| -0.110952  | -0.054301 | ...
# ...
def save_weights(
        features: list[str],
        house_w: dict[str, dict[str, float]],
        min_max: dict[str, dict[str, float]]
        ) -> None:
    try:
        with open(WEIGHTS_PATH, mode='w', newline='') as file:
            writer = csv.DictWriter(file, fieldnames=["Key"] + features)
            writer.writeheader()
            for bound in ["min", "max"]:
                row = {name: min_max[name][bound] for name in features}
                writer.writerow({"Key": bound, **row})
            for house in HOUSES:
                writer.writerow({"Key": house, **house_w[house]})
    except IOError as ioe:
        logging.critical(f"Error writing trained data: {ioe}")
        sys.exit(-1)
    logging.info(f"Saved trained data to {WEIGHTS_PATH}")


# Plot cost per epoch for each house, cost must go down and flatten out
# A curve that rises means the step is too big or a sign is flipped,
# a curve still falling at the last epoch means EPOCHS is too small.
def draw_history(history: dict[str, list[float]]) -> None:
    for house in HOUSES:
        graph.plot(history[house], label=house, color=COLORS[house])
    graph.title(f"Training history (step {STEP}, {EPOCHS} epochs)")
    graph.xlabel("Epoch")
    graph.ylabel("Cost J(θ)")
    graph.legend()
    graph.grid(alpha=0.3)
    save_fig("model", "training_history.png")
    # graph.show()


def chunks(rows, size):
    for start in range(0, len(rows), size):
        yield rows[start:start + size]


# Cost function:
# J(θ) = −(1/m) Σᵢ₌₁..m [ y⁽ⁱ⁾·log(hθ(x⁽ⁱ⁾)) + (1 − y⁽ⁱ⁾)·log(1 − hθ(x⁽ⁱ⁾)) ]
# i is individual student, so x⁽ⁱ⁾ just mean values of current student
# Plug in hypothesis on the right most side
# y⁽ⁱ⁾·log(hypothesis(x⁽ⁱ⁾)) + (1 − y⁽ⁱ⁾)·log(1 − hypothesis(x⁽ⁱ⁾))
# y is binary truth (so 1 or 0, in this case, belong to this house or not)
# If y == 1 -> 1·log(hypothesis) + 0·log(1−hypothesis) -> log(hypothesis)
# If y == 0 -> 0·log(hypothesis) + 1·log(1−hypothesis) -> log(1−hypothesis)
# log() is used here because the closer the value passed to log is to 0,
# the more negative log() gets (log(0.5) = -0.69 but log(0.01) = -4.6),
# so being more wrong costs more than being slightly wrong
# Σᵢ₌₁..m is the sum of all student,
# this mean we do the above for all student then sum them up
# (1/m) the sum divide to total student for average
# Since log() is negative, - at the start mean to flip to positive,
# this mean the smaller J is the better, best case is J = 0
# A wall of explanation for 7 lines of code, yay math!
# This return a number that tell how wrong the current weights are over the
# whole set, the lower the better.
def cost(
        rows: list[tuple[str, dict[str, float]]],
        house: str,
        weights: dict[str, float]
        ) -> float:
    total = 0.0
    for label, values in rows:
        y = 1.0 if label == house else 0.0  # same house -> true
        p = hypothesis(weights, values)
        p = min(max(p, 1e-15), 1 - 1e-15)
        total += y * math.log(p) + (1 - y) * math.log(1 - p)
    return -total / len(rows)


# Formula: hθ(x) = g(θᵀx)
# θ is the list of weight, x is list of value,  θᵀx is their dot product
# so right side will be evaluated to θ1*x1 + θ2*x2 + θ3*x3 + ... + θn*xn
# Used to score student, result is sent to sigmoid() to convert to probability
def hypothesis(weights: dict[str, float], values: dict[str, float]) -> float:
    z = 0.0
    for feature in weights:
        z += weights[feature] * values[feature]
    return sigmoid(z)


# Formula: g(z) = 1 / (1 + e^(-z))
# Used to map any real-valued number into a value between 0 and 1
# https://www.w3schools.com/python/ref_math_exp.asp
def sigmoid(z: float) -> float:
    if z < 0:
        e = math.exp(z)
        return e / (1 + e)
    return 1 / (1 + math.exp(-z))


# Calculate min and max for normalize
def cal_min_max(
        features: list[str],
        data: dict[str, list[str]]
        ) -> dict[str, dict[str, float]]:
    min_max = {}
    for name in features:
        values = sorted(float(v) for v in data[name] if v != '')
        min_max[name] = {"min": cal_min(values), "max": cal_max(values)}
    return min_max


def normalize(
        features: list[str],
        data: dict[str, list[str]],
        min_max: dict[str, dict[str, float]]
        ) -> dict[str, list[float]]:
    normalized = {}
    for name in features:
        low = min_max[name]["min"]
        high = min_max[name]["max"]
        span = high - low
        column = []
        for v in data[name]:
            if v == '' or span == 0:
                column.append(0.5)
            else:
                column.append((float(v) - low) / span)
        normalized[name] = column
    return normalized


# Convert column based read dataset to row based
def convert_to_rows(
        features: list[str],
        data: dict[str, list[str]],
        normalized: dict[str, list[float]]
        # dict[str, float]-> str: nom feature, float: valeur
        # tuple[str, dict[]] -> str: nom de la maison
        ) -> list[tuple[str, dict[str, float]]]:
    rows = []
    houses = data.get("Hogwarts House")
    if not houses or not any(h in HOUSES for h in houses):
        logging.critical('Missing or empty "Hogwarts House" column')
        sys.exit(-1)
    for i in range(len(houses)):
        values = {}
        for feature in features:
            values[feature] = normalized[feature][i]
        rows.append((houses[i], values))
    return rows


if __name__ == "__main__":
    train()
