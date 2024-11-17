package com.scuse.model;

import java.util.ArrayList;
import java.util.List;

public class ConstraintEquation {
    private final List<Double> coefficients;
    private String sign;
    private double constant;

    public ConstraintEquation() {
        coefficients = new ArrayList<>();
    }

    public List<Double> getCoefficients() {
        return coefficients;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public void setCoefficients(List<Double> constraintCoefficients) {
        coefficients.clear();
        coefficients.addAll(constraintCoefficients);
    }
}
