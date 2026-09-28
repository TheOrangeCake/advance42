import math


def cal_count(values: list[float]) -> float:
    if len(values) == 0:
        return float('nan')
    number = 0
    for value in values:
        if value is not None and not math.isnan(value):
            number += 1
    return number


# mean is the sum of the data divided by the number of data points.
def cal_mean(values: list[float]) -> float:
    if values is None or len(values) == 0:
        return float('nan')
    occurrences = 0
    total = 0
    try:
        for value in values:
            if value is not None and not math.isnan(value):
                occurrences += 1
                total += value
        mean = total / occurrences
    except ZeroDivisionError:
        return float('nan')
    return mean


# The value below which approximately p% of the data falls
def cal_std(values: list[float], count: int, mean: float) -> float:
    if len(values) == 0 or count < 2:
        return float('nan')
    squared_differences = 0.0
    for value in values:
        if value is not None and not math.isnan(value):
            squared_differences += (value - mean) ** 2
    standard_deviation = math.sqrt((squared_differences / (count - 1)))
    return (standard_deviation)
