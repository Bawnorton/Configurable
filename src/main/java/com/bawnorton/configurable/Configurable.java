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
     * Minimum value for a numeric field. Must be at most the same value as {@link Configurable#max}.<br>
     */
    double min() default Double.MIN_NORMAL;

    /**
     * Max value for a numeric field. Must be at minimum the same value as {@link Configurable#min}.
     */
    double max() default Double.MAX_VALUE;

    /**
     * For UI generation set the category of this specific element to "config_name.category.%category%".<br>
     * Defaults to the enclosing package name: "config_name.category.package_name".<br>
     * <br>
     * Example:<br>
     * <pre>
     * {@code
     *     package com.bawnorton.package_name;
     *
     *     public class Holder {
     *         @Configurable(category = "test")
     *         public static int value;
     *         @Configurable
     *         public static boolean otherValue;
     *     }
     * }
     * </pre>
     * {@code Holder#value} will be placed in "config_name.category.test" and {@code Holder#otherValue} will be placed in
     * "config_name.category.package_name".
     */
    String category() default "";

    /**
     * Whether or not to exclude the field from the UI generator
     */
    boolean exclude() default false;

    ControllerType controller() default ControllerType.AUTO;
}
