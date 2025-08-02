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
     * This is used to determine if the value parsed from the config file is valid<br>
     * @see Validator
     */
    Validator validator() default @Validator;
}
