# Simplex Calculator - JavaFX Application
#### This is a practical project of the software engineering course of the College of Computer Science of Sichuan University.

## Introduction

This application is designed to help users solve **Linear Programming (LP)** problems and **Optimization Problems** using the **Simplex Algorithm**. The Simplex Algorithm is an efficient method for solving LP problems that involve finding the maximum or minimum of a linear function subject to a set of linear inequalities.

The application is built using **JavaFX**, providing an interactive graphical user interface (GUI) for easy problem input, calculation, and result visualization.

## Features

- **Linear Programming Solver**: Solve LP problems involving multiple constraints and objective functions.
- **Simplex Algorithm Implementation**: The core algorithm that helps in efficiently finding the optimal solution to linear programming problems.
- **Interactive GUI**: User-friendly interface with input fields for constraints, objective function, and other parameters.
- **Result Display**: Once the algorithm converges, the application shows the optimal solution and its corresponding objective function value.

## Requirements

To run the application, the following are required:
- **Java Development Kit (JDK)**: Java 22.
- **JavaFX**: Ensure JavaFX is installed and configured in your development environment.
- **IDE**: Any IDE that supports JavaFX, such as IntelliJ IDEA, Eclipse, or NetBeans.

## Installation

1. Clone the repository or download the source code.
2. Ensure that your Java development environment is set up with JavaFX.
3. Open the project in your preferred IDE.
4. Build and run the project.

## How to Use

1. **Input Data**:
    - Enter the objective function (maximize or minimize).
    - Add the constraints by specifying the inequalities and their coefficients.
    - Choose the type of problem (e.g., maximization or minimization).

2. **Start Calculation**:
    - Click the **Solve** button to begin the Simplex Algorithm process.
    - The application will display the intermediate steps, including pivot operations and changes in basic and non-basic variables.

3. **View Results**:
    - After the algorithm completes, the optimal solution (if one exists) will be displayed, showing the values of the decision variables and the corresponding optimal objective value.
    - Graphical representation of the feasible region and optimal solution will be shown (for two-variable problems).

## Example

### Linear Programming Problem:
Maximize:
$z=3x+2y$

Subject to:

$$
\begin{cases}
x+y &\leq 4 \\
2x+y &\leq 5
\end{cases}
$$


### Steps:
1. Enter the number of variables and constraint functions 2 and 2 in the text input.
2. Input the coefficient of each function.
3. Input the objective function.
4. Choose the solution type maximum.
5. Hit the **Solve** button to begin the Simplex algorithm.
6. The app will solve for the optimal values of `x` and `y` and display the optimal value of `Z`.

## Simplex Algorithm Overview

The **Simplex Algorithm** is an iterative method used to solve linear programming problems. It operates on a **standard form** of the LP problem and moves from one vertex of the feasible region to an adjacent one in such a way that the objective function is improved (maximized or minimized) at each step.

## TODO

**There are currently some bugs in this algorithm， especially solving minimum problem** 

## Acknowledgements

- Simplex Algorithm reference: [Wikipedia - Simplex Method](https://en.wikipedia.org/wiki/Simplex_algorithm)
- JavaFX documentation: [JavaFX Documentation](https://openjfx.io/)


