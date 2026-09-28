import math


# Count missing values
def cal_nan_count(values: list[float]) -> float:
    if len(values) == 0:
        return float('nan')

    count = 0
    for value in values:
        if value is None or math.isnan(value):
            count += 1
            continue
        try:
            float(value)
        except ValueError:
            count += 1
    return float(count)


# Range: the difference between the maximum and minimum values in the data.
def cal_range(values: list[float]) -> float:
    if len(values) == 0:
        return float('nan')
    max_value = float('-inf')
    min_value = float('inf')
    for value in values:
        if value is not None and not math.isnan(value) and value > max_value:
            max_value = value
        if value is not None and not math.isnan(value) and value < min_value:
            min_value = value
        else:
            continue
    if max_value == float('-inf') or min_value == float('inf'):
        return float('nan')
    return (max_value - min_value)
