# ft_linear_regression

A machine learning project that implements **linear regression with gradient descent** from scratch in Python to predict car prices based on mileage.

Created by Nguyen NGUYEN (hoannguy) from 42 Lausanne.

---

## What It Does

Given a dataset of car prices and their mileages, the program:
1. **Trains** a linear model using gradient descent
2. **Visualizes** the regression line, parameter evolution, and training loss
3. **Predicts** the price of a car for any mileage you input

The core prediction formula is:

```
estimatePrice(mileage) = θ₀ + (θ₁ × mileage)
```

---

## Getting Started

**Dependencies:**
```bash
pip install colorama matplotlib
```

**Run the full pipeline (train → visualize → predict):**
```bash
cd src
python main.py
```

**Run individual programs:**
```bash
# Train only
cd src/trainer && python main.py

# Predict only (requires prior training)
cd src/predictor && python main.py

# Clean all generated files
cd src && bash clean.sh
```

---

## Project Structure

```
ft_linear_regression/
├── src/
│   ├── main.py              # Entry point — runs all three programs in sequence
│   ├── clean.sh             # Deletes all generated files
│   ├── trainer/             # Gradient descent training
│   │   ├── data.csv         # Dataset: 24 entries of (km, price in CHF)
│   │   └── ...
│   ├── predictor/           # Interactive price prediction
│   └── bonus/               # Graphs and R² analysis
└── subjects/
    ├── data.csv
    └── en.subject.pdf
```

---

## How It Works

### Training

Data is normalized to `[0, 1]` to prevent numerical instability from the large difference in scale between kilometers and prices. Gradient descent then iteratively updates θ₀ (intercept) and θ₁ (slope) over 100 iterations at a learning rate of 1.7.

See [trainer README](src/trainer/README.md) for the full gradient descent formulas.

### Prediction

After training, the predictor loads the saved θ values and normalization bounds, prompts you for a mileage, and returns the estimated price.

See [predictor README](src/predictor/README.md) for the linear function details.

### Bonus: Visualizations

Three graphs are generated:

| File | Content |
|------|---------|
| `bonus/graph_1.jpg` | Scatter plot of the dataset with the regression line |
| `bonus/graph_2.jpg` | Evolution of θ₀ and θ₁ across training iterations |
| `bonus/graph_3.jpg` | Mean Squared Error (MSE) reduction over iterations |

The R² coefficient is also printed to show how well mileage explains price variation (e.g. R² = 0.72 means 72% of price differences are explained by mileage).

See [bonus README](src/bonus/README.md) for details on each metric.

---

## Dataset

`src/trainer/data.csv` — 24 real car entries:

| Field | Range |
|-------|-------|
| km (mileage) | 22,899 – 240,000 |
| price (CHF) | 3,650 – 8,290 |

---

## Generated Files

After running, the following files are created (all ignored by `.gitignore`):

| File | Description |
|------|-------------|
| `trainer/thetas.csv` | Trained θ₀, θ₁ and normalization bounds |
| `trainer/thetas_list.csv` | Per-iteration θ history for graph 2 |
| `trainer/cost.csv` | Per-iteration MSE for graph 3 |
| `bonus/graph_1.jpg` | Regression visualization |
| `bonus/graph_2.jpg` | Parameter convergence |
| `bonus/graph_3.jpg` | Cost evolution |
