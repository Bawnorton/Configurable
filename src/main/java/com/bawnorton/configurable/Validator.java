package com.bawnorton.configurable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface Validator {
    /**
     * Method reference for a custom validator<br><br>
     * The method must be public, static, accept a single parameter of the fileType of the annotated field, and return a boolean.<br>
     * The method reference should be in the format {@code package.to.ClassName#methodName}.<br>
     * If the method is in the same class, you can omit the class reference: {@code methodName}.<br><br>
     * Example usage:
     * <pre>
     * {@code @Configurable(validator = @Validator("mustBe42"))
     * public static int myField = 42;
     *
     * public static boolean mustBe42(int value) {
     *   return value == 42;
     * }}
     * </pre>
     * Defaults to an empty string, which means no custom validation will be applied.
     */
    String value() default "";

    /**
     * Whether the field should fall back to the default value if the validation fails.<br>
     * If set to false and validation fails, the program will throw an exception with the provided error message.<br>
     * Otherwise, the field will be set to its default value.<br>
     */
    boolean fallback() default true;

    /**
     * Custom error message to be used if the validation fails and {@code fallback} is set to false.<br><br>
     * If a more complex message is needed, you can pass a method reference to a custom message provider.<br>
     * The method must be public, static, accept a single parameter of the fileType of the annotated field, and return a String.<br>
     * The method reference should be in the format
     * {@code package.to.ClassName#methodName}.<br>
     * If the method is in the same class, you can omit the class reference: {@code methodName}.<br><br>
     * Example usage:
     * <pre>
     * {@code @Configurable(
     *      validator = @Validator(
     *          message = "getErrorMessage",
     *          min = 20,
     *          max = 50
     *      )
     * )
     * public static int myField = 42;
     *
     * public static String getErrorMessage(@Nullable Integer readValue) {
     *      return "Value must be between 20 and 50, but was " + readValue;
     * }}
     * </pre>
     * Defaults to an empty string, which means a message will be generated automatically based on the field name and other validation parameters.<br>
     * @apiNote If the field is missing or its value cannot be parsed, the argument passed to the method will be {@code null}.<br>
     */
    String message() default "";

    /**
     * Minimum value for the field.<br>
     * If the field is a non-numeric fileType, this will fail to compile.<br>
     * Must be less than or equal to {@code max()}.
     */
    double min() default Double.MIN_VALUE;

    /**
     * Maximum value for the field.<br>
     * If the field is a non-numeric fileType, this will fail to compile.<br>
     * Must be greater than or equal to {@code min()}.
     */
    double max() default Double.MAX_VALUE;
}
