package com.bawnorton.configurable.ref;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.load.IllegalConfigException;
import com.bawnorton.configurable.ref.constraint.ConstraintSet;
import com.bawnorton.configurable.ref.constraint.ReferenceConstraint;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;

public class Reference<T> {
    private final String name;
    private final Class<?> refHolder;
    private final VarHandle varHandle;
    private final ConstraintSet constraints;
    private final T defaultValue;

    private Object memento;

    public Reference(String name, Class<?> refHolder, Class<T> refType, ConstraintSet.Builder builder) {
        this.name = name;
        this.refHolder = refHolder;
        try {
            this.constraints = builder.build(refHolder, refType);
        } catch (RuntimeException e) {
            throw new IllegalConfigException("Could not build constraints for \"%s\" in \"%s\"".formatted(
                    name,
                    refHolder.getSimpleName()
            ), e);
        }
        try {
            MethodHandles.Lookup publicLookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(refHolder, publicLookup);
            varHandle = privateLookup.findStaticVarHandle(refHolder, name, refType);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        defaultValue = get();
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

    public T getDefault() {
        return defaultValue;
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

    public void setMemento(Object value) {
        memento = value;
    }

    public void applyMemento() {
        if(memento != null) {
            set(memento);
        }
    }

    public void update(boolean fromServer, boolean serverEnforces) {
        if (fromServer == serverEnforces) {
            applyMemento();
        } else {
            memento = null;
        }
    }

    public Class<?> getType() {
        return varHandle.varType();
    }

    public String getName() {
        return name;
    }
}
