package com.bawnorton.configurable.impl.ref.constraint;

public class ClampedReferenceConstraint extends ReferenceConstraint {
    protected final double min;
    protected final double max;

    public ClampedReferenceConstraint(double min, double max, Class<?> refHolder, Class<?> refType) {
        super(refHolder, refType);
        this.min = min;
        this.max = max;
    }

    @Override
    public Object apply(Object value) {
        try {
            double num = Double.parseDouble(value.toString());
            return num > max ? DEFAULT : num < min ? DEFAULT : value;
        } catch (NumberFormatException e) {
            return DEFAULT;
        }
    }
}
