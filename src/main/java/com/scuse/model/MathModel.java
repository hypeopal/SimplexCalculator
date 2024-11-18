package com.scuse.model;

import java.util.ArrayList;
import java.util.List;

public class MathModel {
    private ObjectiveFunction objectiveFunction;
    private final List<ConstraintEquation> constraints;
    private LPQ lpq;

    public MathModel() {
        objectiveFunction = new ObjectiveFunction();
        constraints = new ArrayList<>();
        lpq = new LPQ();
    }

    public ObjectiveFunction getObjectiveFunction() {
        return objectiveFunction;
    }

    public void setObjectiveFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public List<ConstraintEquation> getConstraints() {
        return constraints;
    }

    public void addConstraint(ConstraintEquation constraint) {
        constraints.add(constraint);
    }

    public LPQ getLPQ() {
        return lpq;
    }

    public void solve() {
        int numVariables = objectiveFunction.getCoefficients().size();
        int numConstraints = constraints.size();

        // 创建单纯形表
        int rows = numConstraints + 1; // 包含目标函数行
        int cols = numVariables + numConstraints + 1; // 包含松弛变量和常数列

        double[][] tableau = new double[rows][cols];

        // 填充目标函数行
        for (int i = 0; i < numVariables; i++) {
            tableau[0][i] = -objectiveFunction.getCoefficients().get(i); // 最大化转换为最小化
        }


        // 填充约束方程
        for (int i = 0; i < numConstraints; i++) {
            ConstraintEquation constraint = constraints.get(i);
            if (constraint.getSign().equals("<")) {
                constraint.setSign("<=");
                constraint.setConstant(constraint.getConstant() + 1e-6);
            }
            if (constraint.getSign().equals(">")) {
                constraint.setSign(">=");
                constraint.setConstant(constraint.getConstant() - 1e-6);
            }
            tableau[i + 1][numVariables + numConstraints] = constraint.getConstant();
            for (int j = 0; j < numVariables; j++) {
                tableau[i + 1][j] = constraint.getCoefficients().get(j);
            }
            // 根据约束符号处理松弛变量
            switch (constraint.getSign()) {
                case "<=":
                    tableau[i + 1][numVariables + i] = 1;  // 添加松弛变量
                    break;
                case ">=":
                    tableau[i + 1][numVariables + i] = -1; // 添加松弛变量 (负号)
                    break;
                case "=":
                    tableau[i + 1][numVariables + i] = 0;  // 不添加松弛变量
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported constraint operator: " + constraint.getSign());
            }
            tableau[i + 1][cols - 1] = constraint.getConstant();
        }

        // 开始单纯形法求解
        while (true) {
            // 找到主列 (pivot column)
            int pivotCol = -1;
            double minValue = 0;
            for (int j = 0; j < cols - 1; j++) {
                if (tableau[0][j] < minValue) {
                    minValue = tableau[0][j];
                    pivotCol = j;
                }
            }
            if (pivotCol == -1) {
                // 所有系数均为非负，当前解为最优解
                break;
            }

            // 找到主行 (pivot row)
            int pivotRow = -1;
            double minRatio = Double.MAX_VALUE;
            for (int i = 1; i < rows; i++) {
                if (tableau[i][pivotCol] > 0) {
                    double ratio = tableau[i][cols - 1] / tableau[i][pivotCol];
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        pivotRow = i;
                    }
                }
            }
            if (pivotRow == -1) {
                throw new IllegalArgumentException("Unbounded solution");
            }

            // 主元归一化
            double pivotValue = tableau[pivotRow][pivotCol];
            for (int j = 0; j < cols; j++) {
                tableau[pivotRow][j] /= pivotValue;
            }

            // 消元
            for (int i = 0; i < rows; i++) {
                if (i != pivotRow) {
                    double factor = tableau[i][pivotCol];
                    for (int j = 0; j < cols; j++) {
                        tableau[i][j] -= factor * tableau[pivotRow][j];
                    }
                }
            }
        }

        // 提取解
        List<Double> variableValues = new ArrayList<>();
        for (int i = 0; i < numVariables; i++) {
            double value = 0;
            for (int j = 1; j < rows; j++) {
                if (tableau[j][i] == 1) {
                    value = tableau[j][cols - 1];
                    break;
                }
            }
            variableValues.add(value);
        }

        double objectiveValue = tableau[0][cols - 1];
        lpq.addSolution(objectiveValue, variableValues);
    }

    public void printFunction() {
        System.out.println("Objective Function:");
        System.out.println("Coefficients: " + objectiveFunction.getCoefficients());
        System.out.println("Optimization Type: " + objectiveFunction.getOptimizationType());
        System.out.println("Constraints:");
        for (int i = 0; i < constraints.size(); i++) {
            System.out.println("Constraint " + (i + 1) + ":");
            System.out.println("Coefficients: " + constraints.get(i).getCoefficients());
            System.out.println("Sign: " + constraints.get(i).getSign());
            System.out.println("Constant: " + constraints.get(i).getConstant());
        }
    }

    public void printSolution() {
        System.out.println("LPQ Solution:");
        System.out.println(lpq.toString());
    }
}
