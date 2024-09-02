package com.bawnorton.configurable.impl.ref.constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexReferenceConstraint extends ReferenceConstraint {
    private final Pattern pattern;

    protected RegexReferenceConstraint(String regex, Class<?> refHolder, Class<?> refType) {
        super(refHolder, refType);
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public Object apply(Object value) {
        Matcher matcher = pattern.matcher(value.toString());
        if (matcher.matches()) {
            return value;
        }
        return DEFAULT;
    }
}
