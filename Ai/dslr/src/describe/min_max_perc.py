from math import floor
import math


def cal_min(values: list[float]) -> float:
    if not values:
        return float('nan')
    min_value = float('inf')
    for value in values:
        if value is not None and not math.isnan(value) and value < min_value:
            min_value = value
    if min_value == float('inf'):
        return float('nan')
    return min_value


def cal_max(values: list[float]) -> float:
    if not values:
        return float('nan')
    max_value = float('-inf')
    for value in values:
        if value is not None and not math.isnan(value) and value > max_value:
            max_value = value
    if max_value == float('-inf'):
        return float('nan')
    return max_value


# The value below which approximately p% of the data falls
def cal_per(values: list[float], p: int) -> float:
    if not values:
        return float('nan')
    if p < 0 or p > 100:
        return float('nan')

    clean_val = []
    for value in values:
        if value is not None and not math.isnan(value):
            clean_val.append(value)
    if not clean_val:
        return float('nan')

    clean_val.sort()
    position = (len(clean_val) - 1) * p / 100
    lower = floor(position)
    diff = position - lower
    if diff == 0:
        return clean_val[lower]
    return clean_val[lower] + diff * (clean_val[lower + 1] - clean_val[lower])
