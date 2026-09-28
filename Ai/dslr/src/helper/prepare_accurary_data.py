#!/usr/bin/env python3

import csv


def create_accuracy_test_file(source_train: str,) -> None:
    with open(source_train, "r", newline="") as src:
        reader = csv.DictReader(src)

        if reader.fieldnames is None:
            raise ValueError("Missing CSV header")

        destination = "src/datasets/accuracy_dataset_test.csv"
        with open(destination, "w", newline="") as dst:
            writer = csv.DictWriter(dst, fieldnames=reader.fieldnames)
            writer.writeheader()

            for row in reader:
                row["Hogwarts House"] = ""
                writer.writerow(row)
