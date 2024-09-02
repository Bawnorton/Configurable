package com.bawnorton.configurable.impl.ref.constraint;

import com.bawnorton.configurable.impl.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConstraintSet {
    private final Set<ReferenceConstraint> constraints;

    private ConstraintSet(Set<ReferenceConstraint> constraints) {
        this.constraints = constraints;
    }

    public Object apply(Object value) {
        Object pre = value;
        for(ReferenceConstraint constraint : constraints) {
            value = constraint.apply(value);
            if (value != pre && value != ReferenceConstraint.DEFAULT) {
                throw new IllegalStateException("Constraint \"%s#apply\" did not return \"value\" or \"DEFAULT\"".formatted(constraint.getClass().getSimpleName()));
            }
        }
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<Pair<Double, Double>> clamps = new HashSet<>();
        private final Set<Predicate<Object>> predicates = new HashSet<>();
        private final Set<String> regexes = new HashSet<>();

        public Builder addClamped(Double min, Double max) {
            clamps.add(Pair.of(min, max));
            return this;
        }

        public Builder addPredicate(Predicate<Object> predicate) {
            predicates.add(predicate);
            return this;
        }

        public Builder addRegex(String regex) {
            regexes.add(regex);
            return this;
        }

        public ConstraintSet build(Class<?> refHolder, Class<?> refType) {
            Set<ReferenceConstraint> constraints = new HashSet<>();
            clamps.forEach(clamp -> constraints.add(new ClampedReferenceConstraint(clamp.a(), clamp.b(), refHolder, refType)));
            predicates.forEach(predicate -> constraints.add(new PredicateReferenceConstraint(predicate, refHolder, refType)));
            regexes.forEach(regex -> constraints.add(new RegexReferenceConstraint(regex, refHolder, refType)));
            return new ConstraintSet(constraints);
        }
    }
}
