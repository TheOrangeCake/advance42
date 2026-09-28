DATA_TEST = src/datasets/dataset_test.csv
DATA_TRAIN = src/datasets/dataset_train.csv
DATA_DUMMY = src/datasets/dummy.csv
DATA_DUMMY_2 = src/datasets/dummy2.csv
DATA_DUMMY_TEST = src/datasets/dummy_test.csv
DATA_ACCURACY_TEST = src/datasets/accuracy_dataset_test.csv

PYTHON = venv/bin/python3
PIP = venv/bin/pip
FLAKE = venv/bin/flake8

all: describe histogram scatter pair accuracy

describe:
	$(PYTHON) src/describe/describe.py $(DATA_TRAIN)

histogram:
	$(PYTHON) src/visualization/histogram.py $(DATA_TRAIN)

scatter:
	$(PYTHON) src/visualization/scatter.py $(DATA_TRAIN)

pair:
	$(PYTHON) src/visualization/pair_plot.py $(DATA_TRAIN)

train:
	$(PYTHON) src/model/logreg_train.py $(DATA_TRAIN) $(ARGS)

predict:
	$(PYTHON) src/model/logreg_predict.py $(DATA_TEST)

accuracy:
	$(PYTHON) src/model/accuracy.py $(DATA_TRAIN) $(DATA_ACCURACY_TEST) $(ARGS)

require:
	$(PIP) freeze > requirements.txt

install:
	$(PYTHON) -m venv venv
	$(PIP) install -r requirements.txt

flake:
	$(FLAKE) src

dummy:
	$(PYTHON) src/describe/describe.py $(DATA_DUMMY)

dummy2:
	$(PYTHON) src/describe/describe.py $(DATA_DUMMY_2)

fclean:
	rm -rf plots
	rm -f weights.csv houses.csv

.PHONY: all describe histogram scatter pair train predict accuracy require install flake dummy dummy2 fclean