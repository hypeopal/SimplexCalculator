package com.scuse.model;

import java.util.ArrayList;
import java.util.List;

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
