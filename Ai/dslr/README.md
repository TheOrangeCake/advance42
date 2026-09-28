*This project has been created as part of the 42 advanced curriculum by hoannguy and sforster*
# advance42_dslr

Datascience X Logistic Regression: rebuild a Hogwarts Sorting Hat from student
grades, using a logistic regression trained with gradient descent.

`src/describe` and `src/visualization` are written with the standard library
only (`csv`, `math`) apart from `matplotlib` for the plots — every statistic
is computed by hand, no `pandas`. `src/model` uses `numpy` for the array math
around the same hand-written formulas (sigmoid, cost, gradient); `scikit-learn`
is used in exactly one place, `accuracy.py`, to cross-check the final accuracy
score against a trusted implementation.

## Install

```sh
python3 -m venv venv
venv/bin/pip install -r requirements.txt
```

The `Makefile` calls `venv/bin/python3` directly, so activating the venv is only
needed when running things by hand:

```sh
source venv/bin/activate
```

(`make install` does the same two steps, but only once `venv/` already exists —
it invokes `venv/bin/python3` to create the venv it is supposed to be creating.)

## Run

Most targets run against `src/datasets/dataset_train.csv`; `make predict` uses
`dataset_test.csv`, and `make accuracy` trains on `dataset_train.csv` then
classifies `accuracy_dataset_test.csv` — the same 1600 students, same order,
just without knowing it, so the predictions can be checked row by row against
the real houses in `dataset_train.csv`:

| Command          | What it does                                                |
| ---------------- | ------------------------------------------------------------ |
| `make describe`  | Print per-feature statistics                                |
| `make histogram` | Score distribution per course, per house                    |
| `make scatter`   | Every feature pair, plus the two most similar features      |
| `make pair`      | Pair plot: scatter grid + per-course histogram on the diagonal |
| `make train`     | Train the model, write `weights.csv`                        |
| `make predict`   | Classify `dataset_test.csv` with `weights.csv`, write `houses.csv` |
| `make accuracy`  | Train + predict + compare against the true houses (`sklearn.metrics.accuracy_score`) |
| `make all`       | `describe`, `histogram`, `scatter`, `pair`, `accuracy`, in order |
| `make install`   | Create the venv and install `requirements.txt`              |
| `make flake`     | Lint `src` with flake8                                      |
| `make require`   | Refresh `requirements.txt` from the venv                    |
| `make fclean`    | Remove `plots/`, `weights.csv`, `houses.csv`                |

`make dummy` and `make dummy2` run `describe` against small edge-case datasets
(empty columns, non numeric columns) to check it does not crash.

Each script also runs standalone and takes the dataset path as its first
argument:

```sh
venv/bin/python3 src/model/logreg_train.py src/datasets/dataset_train.csv
```

Figures are written to `plots/<kind>/` and opened in a window.

## Part 1 — describe

`src/describe/describe.py` reimplements `DataFrame.describe()`. It keeps only
the numeric columns (`only_numeric()` drops `Index` and anything that fails to
parse as a float), sorts each column, then computes:

- `Min`, `Max` — first and last value of the sorted column
- `25%`, `50%`, `75%` — percentiles by linear interpolation between the two
  surrounding values
- `Range` — `Max - Min` (bonus)
- `NaN` — number of empty cells in the raw column (bonus)
- `Count` (`cal_count`) — number of non-missing values
- `Mean` (`cal_mean`) — sum of the values divided by `Count`
- `Std` (`cal_std`) — spread of the values around the mean:

```
σ = √( Σ(x − mean)² / (Count − 1) )
```

`Count - 1` rather than `Count` in the denominator (Bessel's correction): the
dataset is treated as a sample used to estimate a larger population's true
variance, not the population itself — using `Count` would systematically
underestimate the spread.

Empty cells are skipped rather than counted as zero, so a missing grade does not
drag a percentile down.

## Part 2 — visualization

All three scripts colour by house (Gryffindor red, Ravenclaw blue, Hufflepuff
yellow, Slytherin green) and skip the non-course columns (`Index`,
`Hogwarts House`, names, `Birthday`, `Best Hand`).

**Histogram** (`plots/histogram/histogram.png`) — one subplot per course, four
overlaid transparent histograms, one per house. It answers *which course has a
homogeneous score distribution between all four houses?* A course is homogeneous
when the four coloured shapes sit on top of each other: the houses are
indistinguishable there, so the feature carries almost no information for
sorting. **Answer: Arithmancy and Care of Magical Creatures.**

**Scatter** (`plots/scatter/scatter_*.png`, `similar.png`) — every pair of
courses plotted against each other, nine per figure. It answers *what are the
two features that are similar?* Two features are similar when their scatter
collapses onto a straight line, meaning one is a rescaling of the other and the
second adds nothing new. **Answer: Astronomy and Defense Against the Dark Arts**
— `similar.png` shows that pair alone.

**Pair plot** (`plots/pair_plot/pair_plot.png`) — the two answers above in one
figure: a course × course grid, scatter off the diagonal and a per-house
histogram on it, so the same "homogeneous?" / "similar?" questions can be read
across every pair at a glance instead of one figure per question.

## Part 3 — training

`src/model/logreg_train.py` trains **four independent binary classifiers**, one
per house — the one-vs-all strategy. Each one only answers a yes/no question
("is this student a Gryffindor?"), and each one owns its own weight vector plus
a scalar bias.

### Preparing the data

1. **Build a feature matrix** (`select_training_data_features`) — one row per
   student, one column per course, a plain `numpy` array. A missing grade
   becomes `nan` (set during CSV import), not skipped.
2. **Normalize** (`normalize_features`) with z-scores, `(value - mean) / std`,
   reusing `cal_mean`/`cal_std` from Part 1. Without this, Arithmancy
   (values in the tens of thousands) would completely drown Divination (values
   in the single digits) and gradient descent would need a different step size
   per feature to converge. A missing grade becomes `0` — exactly the mean in
   z-score coordinates, the least opinionated guess available. A course whose
   `std` is `0` (or undefined) is normalized to `0` for every student instead
   of dividing by zero.
3. **Binary labels** (`get_binary_labels_for_house`) — per house, `1` for
   students in that house, `0` for everyone else.

### The math

**Hypothesis** — score a student, then squash the score into a probability:

```
hθ(x) = g(w·x + b)      w·x = w₁x₁ + w₂x₂ + ... + wₙxₙ
g(z)  = 1 / (1 + e⁻ᶻ)
```

`w·x` is a plain dot product: multiply each grade by that house's weight for the
course and sum, then add the bias `b`. The sigmoid `g` maps the result into
`(0, 1)`.

**Cost** — how wrong the current weights are over the whole set:

```
J(θ) = −(1/m) Σ [ y·log(hθ(x)) + (1 − y)·log(1 − hθ(x)) ]
```

`y` is 1 when the student belongs to the house being trained, 0 otherwise. Only
one of the two terms survives per student. `log` is what makes confident
mistakes expensive: `log(0.5) = -0.69` but `log(0.01) = -4.6`. The leading minus
flips the sum positive, so **lower is better** and 0 is perfect.

**Gradient descent** — nudge the weights and the bias against the slope of the
cost:

```
wⱼ := wⱕ − α · (1/m) Σ (hθ(x⁽ⁱ⁾) − y⁽ⁱ⁾) · xⱼ⁽ⁱ⁾
b  := b  − α · (1/m) Σ (hθ(x⁽ⁱ⁾) − y⁽ⁱ⁾)
```

The `error = p - y` term does the intuitive thing: predicting 0.9 for a student
who is not in the house gives a large positive error, and each weight is pulled
down in proportion to how much that feature contributed.

### The loop

Weights start at `0`, bias at `0`. For each epoch: shuffle the student order,
then walk it in slices of `batch_size` — for each slice, accumulate the
gradient over just those students, average it, and apply one update to `w` and
`b`. The cost is recomputed once per epoch, over the *whole* training set (not
just the last batch), and recorded into `history`. This repeats independently
for each of the four houses.

### Hyperparameters

Passed on the command line, not hard-coded — `batch_size` is what changes the
algorithm (batch GD vs. mini-batch vs. stochastic), `epoch` and `learning_rate`
are tuned to match:

```sh
make train ARGS="<epochs:int> <learning_rate:float> <batch_size:int>"
```

Defaults when an argument is omitted: `epoch=1000`, `learning_rate=0.01`,
`batch_size=None`. `None` means one batch containing the whole dataset: one
update per epoch, very smooth, many epochs needed. Smaller batches update far
more often per epoch, so they need far fewer epochs to reach the same place —
at the cost of a noisier descent.

### Output

`weights.csv` at the repo root holds everything needed to classify later:

```
House      | Bias    | Arithmancy | Astronomy | ...
Gryffindor | 0.1234  | -0.052190  | 1.363597  | ...
Ravenclaw  | ...
Hufflepuff | ...
Slytherin  | ...
Mean       | 0       | 49634.57   | 39.80     | ...
Std        | 0       | 16679.81   | 520.13    | ...
```

The `Mean`/`Std` rows are saved alongside the weights on purpose: the weights
only mean something in the normalized coordinate system, so any later
prediction has to normalize with **these** values and not with a mean/std
recomputed from its own data.

`plots/model/training_history.png` plots `J(θ)` per epoch for the four houses.
It is the sanity check on the run: the curves must fall and then flatten. A
curve that rises means `learning_rate` is too large (the updates overshoot the
minimum and bounce further out each time); a curve still visibly falling at the
last epoch means `epoch` is too small.

## Part 4 — using the saved weights

`src/model/logreg_predict.py` is the forward half of training with the
gradient step removed:

1. **Load** `weights.csv` (`read_weights`) — the four houses' weights and
   biases, plus the saved `Mean`/`Std` rows. The test dataset's course columns
   must match the training courses exactly, or it refuses to run.
2. **Normalize** each student with the saved `Mean`/`Std` (not values
   recomputed from the file being classified), reusing the training formula.
3. **Score** the student once per house with `predict()` (`sigmoid(w·x + b)`),
   giving four probabilities.
4. **Pick** the house with the highest score (`argmax`) and write
   `Index,Hogwarts House` to `houses.csv`.

Since `dataset_test.csv` has no house labels, `src/model/accuracy.py` measures
accuracy a different way: it trains on `dataset_train.csv`, classifies
`accuracy_dataset_test.csv` (the same students, without their labels), and
compares the predictions against the real houses in `dataset_train.csv` using
`sklearn.metrics.accuracy_score` — the target is at least 98%.

```sh
make accuracy ARGS="<epochs:int> <learning_rate:float> <batch_size:int>"
```

## Resources

- [Logistic regression](https://www.geeksforgeeks.org/machine-learning/understanding-logistic-regression/)
- [Logistic regression](https://www.youtube.com/watch?v=3giTXZbyf1Q)
- [matplotlib](https://matplotlib.org/3.5.3/api/_as_gen/matplotlib.pyplot.html)

## Dev

- Update the dependency list after installing something new:
```sh
pip freeze > requirements.txt
```
- Lint:
```sh
flake8 src
```

## General structure

dslr/
├── README.md                 Project setup instructions
├── Makefile                  Short commands for running each part
├── requirements.txt          External Python packages
├── src/
│   ├── datasets/             Training, testing, and dummy CSV data
│   ├── helper/               Code shared by multiple programs
│   ├── describe/             Statistical analysis
│   ├── visualization/        Histograms, scatter and pair plots
│   └── model/                Logistic-regression training, prediction, accuracy
└── subjects/
    ├── en.subject.pdf        Original school assignment
    └── datasets/             Original copies of the datasets

## The src subdirectories correspond to stages of a typical machine-learning workflow:

CSV data
   ↓
Read and clean data
   ↓
Describe statistics
   ↓
Visualize relationships
   ↓
Train a model
   ↓
Predict classes
