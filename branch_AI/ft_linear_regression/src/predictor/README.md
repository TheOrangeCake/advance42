## Predictor:

- **Goal**: Predict price of a car for a given mileage
- **Behavior requirement**:
    1. Prompt for a mileage
    2. Return price


## How it work

### Linear Function
- **Formular:**
$$estimatePrice(mileage) = \theta_0 + (\theta_1 \times mileage)$$

Linear function will trace a straight line in the graph, that passes by point $(0 + \theta_0)$ and point $(\theta_0 + (\theta_1 * mileage))$.

Variables $\theta_0$ is the intercept and $\theta_1$ is the slope. Which mean the higher the $\theta_0$ is, the higher the line position will be in the graph. The higher the $\theta_1$ is, the steeper the slope will be in the graph. If mileage is 0, then the car price will be $\theta_0$.

- **Why this is relevant?**

Variables $\theta_0$ and $\theta_1$ will be calculated by the [trainer program](../predictor/README.md) using **gradient descent linear regression** based on historical data of car price in relation to car mileage. This mean the linear function drawn using those variables will represent the price/mileage trend, and we can use this function to predict data that don't exist yet more accurately.

### Program flow
1. Get the $\theta_0$, $\theta_1$ and values for denormalize from trainer program
2. Prompt user for a mileage
3. Validate user input
4. Run the linear function which formular is in previous section using imported values
5. Return the result (which is the car price) to user