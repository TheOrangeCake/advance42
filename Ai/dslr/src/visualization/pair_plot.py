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
    get_house_course_grades,
    get_courses_list,
)
from helper.plot import save_fig  # noqa: E402
from histogram import filter_non  # noqa: E402
coloredlogs.install()


COLORS = {
    "Gryffindor": "red",
    "Ravenclaw":  "orange",
    "Hufflepuff": "yellow",
    "Slytherin":  "black",
}


def pair_plot():
    # read data
    if len(sys.argv) != 2:
        logging.critical('Usage: ./pair_plot [Dataset path]')
        return
    else:
        logging.info(f"Pair Plot Dataset source: {sys.argv[1]}")
    data = import_data(sys.argv[1])

    try:
        list_houses: list[str] = get_houses_list(data)
        list_courses: list[str] = get_courses_list(data)
    except (KeyError, ValueError) as e:
        logging.critical(e)
        sys.exit(1)
    figure, axes = graph.subplots(
        len(list_courses), len(list_courses), figsize=(20, 20)
    )
    # loop1 for each feat (course)
    for row, course_y in enumerate(list_courses):
        for col, course_x in enumerate(list_courses):

            # name on axes
            axis = axes[row][col]
            if row == 0:
                axis.set_title(course_x, fontsize=8)
            if col == 0:
                axis.set_ylabel(course_y, fontsize=8)
                axis.yaxis.set_label_coords(-0.15, 0.5)

            if course_x == course_y:
                pair_histogramm(data, course_x, axis, list_houses)
            else:
                pair_scatter(data, course_x, course_y, axis, list_houses)

    handles, labels = graph.gca().get_legend_handles_labels()
    graph.figlegend(handles, labels, loc='lower right')
    for ax in graph.gcf().get_axes():
        ax.set_xticks([])
        ax.set_yticks([])
    save_fig("pair_plot", "pair_plot.png")


def pair_histogramm(data, course_x, axis, list_houses):
    house_scores = {house: [] for house in list_houses}

    for house in list_houses:
        house_scores[house] = get_house_course_grades(data, house, course_x)
        house_scores[house] = filter_non(house_scores[house])
    for house in list_houses:
        axis.hist(
            house_scores[house],
            label=house,
            color=COLORS[house],
            alpha=0.5,
            edgecolor='black',
        )


def pair_scatter(data, course_x, course_y, axis, list_houses):
    for house in list_houses:
        house_scores_x = get_house_course_grades(data, house, course_x)
        house_scores_y = get_house_course_grades(data, house, course_y)
        house_scores_x, house_scores_y = filter_non_pair(
            house_scores_x, house_scores_y
        )
        axis.scatter(
            house_scores_x, house_scores_y,
            s=10, color=COLORS[house], alpha=0.5,
        )
    return


def filter_non_pair(house_scores_x, house_scores_y):
    house_scores_x_new, house_scores_y_new = [], []
    for x, y in zip(house_scores_x, house_scores_y):
        if x is not None and y is not None:
            house_scores_x_new.append(x)
            house_scores_y_new.append(y)
    return house_scores_x_new, house_scores_y_new


if __name__ == "__main__":
    pair_plot()
