# Trainer:

- **Goal**: Train the model using linear regression
- **Behavior requirements**:
    1. Read the data set
    2. Perform linear regression on the data set


## How it work

### Linear regression
- **Formulars:**
    $$tmp\theta_0 = learningRate \times \frac{1}{m} \sum_{i=0}^{m-1}(estimatePrice(mileage[i]) - price[i])$$
    $$tmp\theta_1 = learningRate \times \frac{1}{m} \sum_{i=0}^{m-1}
    (estimatePrice(mileage[i]) - price[i]) \times mileage[i]$$
    
    $\theta_0$ and $\theta_1$ will be set to 0 at the start. For $estimatePrace()$ formular, please refer to [predictor README](../predictor/README.md).

    Explaination:
    - We first calculate the error between predicted value and actual value: $estimatePrice(mileage[i])$ is the car price predicted and $price[i]$ is the actual car price in data set. So $(estimatePrice(mileage[i]) - price[i])$ results in the different.
    - The notation $\sum_{i=0}^{m-1}()$ means $i$ starts at $0$ and stops at $(m-1)$, and $\sum$ is the sum of values inside parentheses $()$. The variable $m$ is the size of the data set. So $\frac{1}{m} \sum_{i=0}^{m-1}(estimatePrice(mileage[i]) - price[i])$ is the sum of all errors divides by the number of data in data set, in another word, the average of all errors.
    - The variable $learningRate$ is for step, it is basically an error correction that help speed up the training. Bigger step mean faster learning but higher change of divergence. Lower step is more accurate but take a lot more time.
    - We run this formular on all rows in data set until the temporal $tmp\theta$ are very close to $\theta$.

- **Gradient descent:**

    You may have noted in the linear regression formular, its result is $tmp\theta$ instead of $\theta$. So to actually update the $\theta$, we need to apply **gradient descent**. The formular is:
    $$\theta = \theta - tmp\theta$$
    Why minus? Because we want to *descent" from the higher position to lower position by moving the the opposite direction, in our case, to get closer to 0. We assume that the current $\theta$ is further from 0 than $tmp\theta$.

### Program flow
1. Import the data from specified csv file. The header need to be "km,price" exactly
2. Validate data format
3. Normalize values because of big disparency between km and price
3. Run linear regression on the dataset using predefined parameters which results in $\theta_0$ and $\theta_1$
4. Export $\theta_0$, $\theta_1$ and values to denormalize to a file