package com.bawnorton.configurable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Configurable {
    /**
     * The name of the config element<br>
     * Defaults to the element name
     */
    String value() default "";

    /**
     * The enclosing group for the config element<br>
     * <br>
     * Sub groups can be created by using a dot notation in the group name.<br>
     * For example, if you want to create a group "myGroup" with a sub
     * group "subGroup", you can use the group name {@code "myGroup.subGroup"}.<br>
     * This will yield a config entry like
     * <pre>
     * {@code "myGroup": {
     *      "subGroup": {
     *          "myField": 42
     *      }
     * }}
     * Defaults to no group
     */
    String group() default "";

    /**
     * Whether the config element should sync with the client<br>
     * Defaults to true
     */
    boolean sync() default true;

    /**
     * Method reference for a listener that will be called when the config element is set to a new value.<br><br>
     * The method must be public, static, accept a parameter of the type of the annotated field and a boolean parameter indicating whether it is set from a sync or not, and return nothing.<br>
     * The method reference should be in the format {@code package.to.ClassName#methodName}.<br>
     * If the method is in the same class, you can omit the class reference: {@code methodName}.<br><br>
     * Example usage:
     * <pre>
     * {@code @Configurable(onSet = "logOnSet")
     * public static int myField = 42;
     *
     * public static void logOnSet(int value, boolean fromSync) {
     *    LOGGER.info("myField was set to " + value + " (sync: " + fromSync + ")");
     * }}
     * </pre>
     * Defaults to an empty string, which means no listener will be called.
     * @apiNote This method is only called when the reference is set, which happens when the config is loaded or synced.<br>
     */
    String onSet() default "";


    /**
     * This is used to determine if the value parsed from the config file is valid<br>
     * @see Validator
     */
    Validator validator() default @Validator;
}
