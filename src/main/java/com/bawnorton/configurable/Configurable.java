package com.bawnorton.configurable;

import com.bawnorton.configurable.impl.IllegalConfigException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Configurable {
    /**
     * The name of the config element.<br>
     * Defaults to the element name.
     */
    String value() default "";

    /**
     * Regex for validating a config value before being set.
     */
    String regex() default "";

    /**
     * Name of a method with the signature: {@code static boolean name(FieldType)} for validating a config value
     * before being set.<br>
     * This method must be static, accept the field type and return a boolean; any other signature will result
     * in a {@link IllegalConfigException} being thrown as the method will not be found.
     */
    String predicate() default "";

    /**
     * Minimum value for a numeric field. Must be at most the same value as {@link Configurable#max}.
     */
    double min() default Double.MIN_NORMAL;

    /**
     * Max value for a numeric field. Must be at minimum the same value as {@link Configurable#min}.
     */
    double max() default Double.MAX_VALUE;

    /**
     * For configuring the UI Generator
     */
    Yacl yacl() default @Yacl;
}
