package com.bawnorton.configurable.impl.ref.constraint;

public abstract class ReferenceConstraint {
    /**
     * Used to indicate a constrained failed<br>
     * Returned by {@link ReferenceConstraint#apply}
     */
    public static final Object DEFAULT = new Object();

    protected final Class<?> refHolder;
    protected final Class<?> refType;

    protected ReferenceConstraint(Class<?> refHolder, Class<?> refType) {
        this.refHolder = refHolder;
        this.refType = refType;
    }

    /**
     * @return {@link ReferenceConstraint#DEFAULT} if violating, otherwise value
     */
    public abstract Object apply(Object value);
}
