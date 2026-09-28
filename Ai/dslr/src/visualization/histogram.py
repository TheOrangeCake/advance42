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
    get_house_course_grades,
    course_data,
)
from helper.plot import save_fig  # noqa: E402
import describe.count_mean_std as cal  # noqa: E402
from describe.bonus_nan_range import cal_range  # noqa: E402


coloredlogs.install()


COLORS = {
    "Gryffindor": "red",
    "Ravenclaw":  "blue",
    "Hufflepuff": "yellow",
    "Slytherin":  "green",
}


def filter_non(values):
    return [v for v in values if v is not None and v == v]


# calculate the spread of each house mean and std.
# normalize it to have the same scale add it.
# More the spread of mean and std are large, more the index is not homogene
def homogeneity_index(data, course, list_houses):
    mean_houses = []
    std_houses = []
    dif_max_min = cal_range(course_data(data, course))
    if (
        dif_max_min == 0
        or dif_max_min != dif_max_min
        or dif_max_min in (float('inf'), float('-inf'))
    ):
        return float('nan')
    for house in list_houses:
        house_course_data = get_house_course_grades(data, house, course)
        house_course_data = filter_non(house_course_data)
        count = cal.cal_count(house_course_data)
        mean = cal.cal_mean(house_course_data)
        std = cal.cal_std(house_course_data, count, mean)
        mean_houses.append(mean)
        std_houses.append(std)

    mean_spread = cal_range(mean_houses)
    std_spread = cal_range(std_houses)

    # normalize
    mean_spread_norm = mean_spread / dif_max_min
    std_spread_norm = std_spread / dif_max_min

    index = mean_spread_norm + std_spread_norm
    return index


def select_course(homogeneity_ind: dict[str, float], list_courses) -> str:
    choosen_course = list_courses[0]
    for course in list_courses:
        if (homogeneity_ind[course] < homogeneity_ind[choosen_course]):
            choosen_course = course
    return choosen_course


def histogram() -> None:
    if len(sys.argv) != 2:
        logging.critical('Usage: ./histogram [Dataset path]')
        return
    else:
        logging.info(f"Histogram Dataset source: {sys.argv[1]}")

    data = import_data(sys.argv[1])
    house_col = data.get("Hogwarts House")
    if not house_col:
        logging.critical('Missing or empty "Hogwarts House" column')
        return
    plot = 1

    try:
        list_houses: list[str] = get_houses_list(data)
        list_courses: list[str] = get_courses_list(data)
    except (KeyError, ValueError) as e:
        logging.critical(e)
        sys.exit(1)

    homogeneity_ind: dict[str, float] = {}
    for course in list_courses:
        homogeneity_ind[course] = homogeneity_index(data, course, list_houses)

        # homogeneity_ind[course] = homogeneity_index(data, course)
        house_scores = {house: [] for house in list_houses}

        for house in list_houses:
            house_scores[house] = get_house_course_grades(data, house, course)
            house_scores[house] = filter_non(house_scores[house])
        # build graphics
        graph.subplot(4, 4, plot)
        graph.xlabel(f"H index: {homogeneity_ind[course]:.3f}")
        plot += 1
        for house in list_houses:
            graph.hist(
                house_scores[house],
                label=house,
                color=COLORS[house],
                alpha=0.5,
                edgecolor='black',
            )
        graph.title(course)
    handles, labels = graph.gca().get_legend_handles_labels()
    graph.figlegend(handles, labels, loc='lower right')
    save_fig("histogram", "histogram.png")
    # graph.show()
    graph.figure()
    plot = 1

    # display the most homogeneous course
    most_homogeneous_course = select_course(homogeneity_ind, list_courses)
    house_scores = {house: [] for house in list_houses}
    for house in list_houses:
        house_scores[house] = get_house_course_grades(
            data, house, most_homogeneous_course
        )
        house_scores[house] = filter_non(house_scores[house])

    # build graphics
    graph.subplot(1, 1, plot)
    graph.xlabel(
        f"H index: {homogeneity_ind[most_homogeneous_course]:.3f} , "
        f"{most_homogeneous_course}"
    )
    for house in list_houses:
        graph.hist(
            house_scores[house],
            label=house,
            color=COLORS[house],
            alpha=0.5,
            edgecolor='black',
        )
    graph.title("most homogeneous course")
    handles, labels = graph.gca().get_legend_handles_labels()
    graph.figlegend(handles, labels, loc='lower right')
    save_fig("histogram", "Most homogeneous score distribution.png")
    # graph.show()


if __name__ == "__main__":
    histogram()
