package com.scuse.model;

import java.util.ArrayList;
import java.util.List;

// 单个解
class Solution {
    private double objectiveValue; // 目标函数值
    private List<Double> variableValues; // 变量值

    public Solution(double objectiveValue, List<Double> variableValues) {
        this.objectiveValue = objectiveValue;
        this.variableValues = new ArrayList<>(variableValues);
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public List<Double> getVariableValues() {
        return variableValues;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Objective Value: ").append(objectiveValue).append("\n");
        result.append("Variable Values: ");
        for (int i = 0; i < variableValues.size(); i++) {
            result.append("x").append(i + 1).append("=").append(variableValues.get(i)).append(" ");
        }
        return result.toString();
    }
}

// 解集
public class LPQ {
    private List<Solution> solutions; // 存储多个解

    public LPQ() {
        this.solutions = new ArrayList<>();
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void addSolution(double objectiveValue, List<Double> variableValues) {
        solutions.add(new Solution(objectiveValue, variableValues));
    }

    @Override
    public String toString() {
        if (solutions.isEmpty()) {
            return "No solutions found.";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < solutions.size(); i++) {
            result.append("Solution ").append(i + 1).append(":\n");
            result.append(solutions.get(i).toString()).append("\n");
        }
        return result.toString();
    }
}
