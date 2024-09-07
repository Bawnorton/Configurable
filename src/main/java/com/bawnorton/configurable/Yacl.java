package com.bawnorton.configurable;

import dev.isxander.yacl3.api.OptionFlag;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Yacl {
    /**
     * <i><b>Inherited</b></i><br>
     * For UI generation set the category of this specific element to "config_name.category.%category%".<br>
     * Defaults to the enclosing package name: "config_name.category.package_name".<br>
     * <br>
     * Example:<br>
     * <pre>
     * {@code
     *     package com.bawnorton.package_name;
     *
     *     public class Holder {
     *         @Configurable(yacl = @Yacl(category = "test"))
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
     * Sets the method to use for setting the description of the displayed value of an option<br>
     * Requirement differs when annotating a field or class:<br>
     * <br>
     * <b>Field</b><br>
     * Name of a method with the following signature:
     * <pre>
     * {@code public static Text name(FieldType value)}
     * </pre>
     * <br>
     * <b>Class</b><br>
     * Name of a method with the following signature:
     * <pre>
     * {@code public static Text name()}
     * </pre>
     * Any other signature will result in a compile time exception.<br>
     * Also accepts referencing a method outside of the current class:<br>
     * <pre>
     * {@code fully.qualified.owner#methodName}
     * </pre>
     */
    String descriptioner() default "";

    /**
     * <i><b>Inherited</b></i><br>
     * Whether or not to exclude the field from the UI generator
     */
    boolean exclude() default false;

    /**
     * Which controller should the field use in the UI
     */
    ControllerType controller() default ControllerType.AUTO;

    /**
     * Sets the method to use for formatting the displayed value of an option<br>
     * <br>
     * Name of a method with the following signature:
     * <pre>
     * {@code public static Text name(FieldType value)}
     * </pre>
     * Any other signature will result in a compile time exception.<br>
     * Also accepts referencing a method outside of the current class:<br>
     * <pre>
     * {@code fully.qualified.owner#methodName}
     * </pre>
     */
    String formatter() default "";

    /**
     * <i><b>Inherited</b></i><br>
     * @see OptionFlag
     */
    OptionType[] type() default {};

    /**
     * Sets the method(s) to use for listening to changes to an option within the config ui<br>
     * <br>
     * Name of a method with the following signature:
     * <pre>
     * {@code public static Text name(FieldType value)}
     * </pre>
     * Any other signature will result in a compile time exception.<br>
     * Also accepts referencing a method outside of the current class:<br>
     * <pre>
     * {@code fully.qualified.owner#methodName}
     * </pre>
     */
    String[] listener() default {};

    /**
     * Whether the option group should be collapsed by default.
     */
    boolean collapsed() default false;

    /**
     * <i><b>Inherited</b></i><br>
     * Add an image to the group/option
     */
    Image image() default @Image;
}
