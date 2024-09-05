package com.bawnorton.configurable;

import dev.isxander.yacl3.api.OptionFlag;

public @interface Yacl {
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
     * Name of a method with the signature: {@code public static List<Text> name(FieldType)} for formatting the description value
     * within the config UI.<br>
     * Defaults to {@code List.of(Text.translatable("configurable.config_name.description.%key%"))}
     * Any other signature will result in a compile time exception.<br>
     * <br>
     * Also accepts referencing a method outside of the current class:<br>
     * {@code fully.qualified.owner#methodName}<br>
     */
    String descriptioner() default "";

    /**
     * Whether or not to exclude the field from the UI generator
     */
    boolean exclude() default false;

    /**
     * Which controller should the field use in the UI
     */
    ControllerType controller() default ControllerType.AUTO;

    /**
     * Name of a method with the signature: {@code public static Text name(FieldType)} for formatting the previewed value
     * within the config UI.<br>
     * Any other signature will result in a compile time exception.<br>
     * <br>
     * Also accepts referencing a method outside of the current class:<br>
     * {@code fully.qualified.owner#methodName}<br>
     */
    String formatter() default "";

    /**
     * @see OptionFlag
     */
    OptionType[] type() default {};

    /**
     * Name of a method with the signature: {@code public static void name(Option<FieldType>, FieldType)} for listening
     * to applied changes from the config UI.<br>
     * Any other signature will result in a compile time exception.<br>
     * <br>
     * Also accepts referencing a method outside of the current class:<br>
     * {@code fully.qualified.owner#methodName}<br>
     */
    String[] listener() default {};

    boolean collapsed() default false;

    Image image() default @Image;
}
