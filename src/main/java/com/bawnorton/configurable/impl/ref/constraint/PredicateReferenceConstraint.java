package com.bawnorton.configurable.impl.ref.constraint;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.impl.IllegalConfigException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class PredicateReferenceConstraint extends ReferenceConstraint {
    private final String predicateName;
    private final MethodHandle predicate;

    public PredicateReferenceConstraint(String predicateName, Class<?> refHolder, Class<?> refType) {
        super(refHolder, refType);
        this.predicateName = predicateName;
        if(predicateName.isBlank()) {
            predicate = null;
        } else {
            try {
                MethodHandles.Lookup publicLookup = MethodHandles.lookup();
                MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(refHolder, publicLookup);
                MethodType refTypeMap = MethodType.methodType(boolean.class, refType);
                predicate = privateLookup.findStatic(refHolder, predicateName, refTypeMap);
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new IllegalConfigException("Could not find or access predicate method \"%s\" in \"%s\"".formatted(predicateName, refHolder.getSimpleName()));
            }
        }
    }

    @Override
    public Object apply(Object value) {
        if(predicate == null) return value;

        try {
            if((boolean) predicate.invoke(value)) {
                return value;
            }
        } catch (Throwable t) {
            ConfigurableMain.LOGGER.error("Could not apply predicate \"%s\" in \"%s\" to \"%s\"".formatted(predicateName, refHolder, value), t);
        }
        return DEFAULT;
    }
}
