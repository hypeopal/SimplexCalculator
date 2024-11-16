package com.scuse.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveFunction {
    private List<Double> coefficients;
    private String optimizationType;

    public ObjectiveFunction() {
        coefficients = new ArrayList<>();
    }

    public List<Double> getCoefficients() {
        return coefficients;
    }

    public String getOptimizationType() {
        return optimizationType;
    }

    public void setOptimizationType(String optimizationType) {
        this.optimizationType = optimizationType;
    }

    public void clear() {
        coefficients.clear();
        optimizationType = "";
    }
}
