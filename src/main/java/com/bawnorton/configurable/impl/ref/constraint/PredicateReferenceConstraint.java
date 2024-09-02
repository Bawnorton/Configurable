package com.bawnorton.configurable.impl.ref.constraint;

import com.bawnorton.configurable.ConfigurableMain;
import java.util.function.Predicate;

public class PredicateReferenceConstraint extends ReferenceConstraint {
    private final Predicate<Object> predicate;

    public PredicateReferenceConstraint(Predicate<Object> predicate, Class<?> refHolder, Class<?> refType) {
        super(refHolder, refType);
        this.predicate = predicate;
    }

    @Override
    public Object apply(Object value) {
        if(predicate == null) return value;

        try {
            if(predicate.test(value)) {
                return value;
            }
        } catch (Throwable t) {
            ConfigurableMain.LOGGER.error("Could not apply predicate in \"%s\" to \"%s\"".formatted(refHolder, value), t);
        }
        return DEFAULT;
    }
}
