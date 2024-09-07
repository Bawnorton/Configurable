package com.bawnorton.configurable;

import com.bawnorton.configurable.load.IllegalConfigException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Configurable {
    /**
     * The name of the config element<br>
     * Defaults to the element name
     */
    String value() default "";

    /**
     * Regex for validating a config value before being set
     */
    String regex() default "";

    /**
     * Sets the method to use for validating a config value before being set<br>
     * <br>
     * Name of a method with the following signature:
     * <pre>
     * {@code public static boolean name(FieldType value)}
     * </pre>
     * Any other signature will result in a compile time exception.<br>
     * Also accepts referencing a method outside of the current class:<br>
     * <pre>
     * {@code fully.qualified.owner#methodName}
     * </pre>
     * @throws IllegalConfigException if the method cannot be found
     */
    String predicate() default "";

    /**
     * Minimum value for a numeric field. Must be at most the same value as {@link Configurable#max}<br>
     */
    double min() default Double.MIN_NORMAL;

    /**
     * Max value for a numeric field. Must be at minimum the same value as {@link Configurable#min}
     */
    double max() default Double.MAX_VALUE;

    /**
     * For configuring the UI Generator
     */
    Yacl yacl() default @Yacl;
}
