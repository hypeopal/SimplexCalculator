package com.scuse.model;

import java.util.ArrayList;
import java.util.List;

public class MathModel {
    private ObjectiveFunction objectiveFunction;
    private final List<ConstraintEquation> constraints;

    public MathModel() {
        objectiveFunction = new ObjectiveFunction();
        constraints = new ArrayList<>();
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

    public void print() {
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
}
