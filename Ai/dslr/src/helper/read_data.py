import csv
import sys
import logging
import coloredlogs
import numpy as np

coloredlogs.install()


def is_numeric_column(values: list[str]) -> bool:
    has_value = False
    for v in values:
        if v == '':
            continue
        has_value = True
        try:
            float(v)
        except ValueError:
            # logging.warning(f"NaN detected: {v}, skipped column")
            return False
    return has_value


# remove non numeric column
def only_numeric(data: dict[str, list[str]]) -> dict[str, list[float]]:
    result = {}
    for name, values in data.items():
        if name.lower() == 'index':
            continue
        if not is_numeric_column(values):
            continue
        result[name] = [float(v) for v in values if v != '']
    return result


def import_data(data_train: str) -> dict[str, list[str]]:
    data = read_dataset(data_train)
    remove_useless_data(data)
    empty_values(data)
    return data


def read_dataset(data_train: str) -> dict[str, list[str]]:
    try:
        with open(data_train, mode='r') as file:
            reader = csv.DictReader(file)
            fields = reader.fieldnames or []
            columns = {name: [] for name in fields}
            for row in reader:
                for name in fields:
                    data = (row[name] or '').strip()
                    columns[name].append(data)
        return columns
    except IOError as ioe:
        logging.critical(f"Error opening file: {ioe}")
        sys.exit(-1)


def remove_useless_data(data: dict[str, list[str]]) -> None:
    for key in ["Index", "First Name", "Last Name", "Birthday", "Best Hand"]:
        data.pop(key, None)


# then it is easier to convert empty values
def empty_values(data: dict[str, list[str]]) -> None:
    for column in data:
        for i in range(len(data[column])):
            if data[column][i] == "":
                data[column][i] = "nan"


# get courses list
def get_courses_list(data: dict[str, list[str]]) -> list[str]:
    courses_list = []
    for i in data:
        if i == "Hogwarts House":
            continue
        courses_list.append(i)
    if not courses_list:
        raise ValueError("Missing or empty courses in dataset")
    return courses_list


# get houses list
def get_houses_list(data: dict[str, list[str]]) -> list[str]:
    houses_list = []
    if ("Hogwarts House") not in data:
        raise KeyError("No Hogwarts House in data set")
    for house in data["Hogwarts House"]:
        if house not in ("", "nan") and house not in houses_list:
            houses_list.append(house)
    if not houses_list:
        raise ValueError('Missing or empty "Hogwarts House" column')
    return houses_list


def nb_students(data: dict[str, list[str]]) -> int:
    if "Hogwarts House" not in data:
        raise KeyError("No Hogwarts House in data set")
    nb_students = len(data["Hogwarts House"])
    if nb_students == 0:
        raise ValueError("No student in dataset")
    return nb_students


# get data by branch in numpy
def course_data(data: dict[str, list[str]], branch: str) -> np.ndarray:
    if branch not in data:
        raise KeyError("This course is not in data set")
    return np.array(data[branch], dtype=float)


# get grades for one house in one branch, in numpy
def get_house_course_grades(
    data: dict[str, list[str]], house: str, branch: str
) -> np.ndarray:
    if branch not in data:
        raise KeyError("This course is not in data set")
    if "Hogwarts House" not in data:
        raise KeyError("No Hogwarts House in data set")
    grades = [
        data[branch][i]
        for i in range(len(data["Hogwarts House"]))
        if data["Hogwarts House"][i] == house
    ]
    return np.array(grades, dtype=float)
