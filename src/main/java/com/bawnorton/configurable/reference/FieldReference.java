package com.bawnorton.configurable.reference;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.util.GenericType;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record FieldReference<T>(Consumer<T> setter, Supplier<T> getter, GenericType genericType, String name, String group, String comment, boolean doesSync, ValidatorReference<T> validator) {
    public void set(T value) {
        setter.accept(value);
    }

    public T get() {
        return getter.get();
    }

    public void load() {
        ConfigurableMain.getCurrentSaveLoader().markToBeLoaded(this);
    }

    public void save() {
        ConfigurableMain.getCurrentSaveLoader().markToBeSaved(this);
    }

    public static <T> Builder<T> builder(Consumer<T> setter, Supplier<T> getter, GenericType genericType, String name) {
        return new Builder<>(setter, getter, genericType, name);
    }

    public static class Builder<T> {
        private final Consumer<T> setter;
        private final Supplier<T> getter;
        private final GenericType genericType;
        private final String name;

        private String group = null;
        private String comment = null;
        private boolean doesSync = false;
        private ValidatorReference<T> validator = null;

        public Builder(Consumer<T> setter, Supplier<T> getter, GenericType genericType, String name) {
            this.setter = setter;
            this.getter = getter;
            this.genericType = genericType;
            this.name = name;
        }

        public Builder<T> group(String group) {
            this.group = group;
            return this;
        }

        public Builder<T> comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder<T> doesSync(boolean doesSync) {
            this.doesSync = doesSync;
            return this;
        }

        public Builder<T> validator(ValidatorReference<T> validator) {
            this.validator = validator;
            return this;
        }

        public FieldReference<T> build() {
            return new FieldReference<>(setter, getter, genericType, name, group, comment, doesSync, validator);
        }
    }
}
