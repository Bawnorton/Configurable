package com.bawnorton.configurable.impl.ref;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.impl.IllegalConfigException;
import com.bawnorton.configurable.impl.ref.constraint.ConstraintSet;
import com.bawnorton.configurable.impl.ref.constraint.ReferenceConstraint;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;

public class Reference<T> {
    private final String name;
    private final Class<?> refHolder;
    private final VarHandle varHandle;
    private final ConstraintSet constraints;

    public Reference(String name, Class<?> refHolder, Class<T> refType, ConstraintSet.Builder builder) {
        this.name = name;
        this.refHolder = refHolder;
        this.constraints = builder.build(refHolder, refType);
        try {
            MethodHandles.Lookup publicLookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(refHolder, publicLookup);
            varHandle = privateLookup.findStaticVarHandle(refHolder, name, refType);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        T defaultValue = get();
        if(Objects.equals(constraints.apply(defaultValue), ReferenceConstraint.DEFAULT)) {
            throw new IllegalConfigException("Default value \"%s\" for \"%s\" in \"%s\" does not conform to it's constraints".formatted(
                    defaultValue,
                    name,
                    refHolder.getSimpleName()
            ));
        }
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) varHandle.get();
    }

    public void set(Object value) throws ClassCastException {
        Object pre = value;
        value = constraints.apply(value);
        if(value != ReferenceConstraint.DEFAULT) {
            varHandle.set(value);
        } else {
            ConfigurableMain.LOGGER.warn("Custom value \"%s\" for \"%s\" in \"%s\" violated it's constraints. Set to default".formatted(
                    pre,
                    name,
                    refHolder.getSimpleName()
            ));
        }
    }

    public Class<?> getType() {
        return varHandle.varType();
    }

    public String getName() {
        return name;
    }
}
