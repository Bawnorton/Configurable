package com.bawnorton.configurable;

import com.google.gson.FieldNamingPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Configurable {
    String key() default "";
    FieldNamingPolicy naming() default FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

    int mini() default Integer.MIN_VALUE;
    int maxi() default Integer.MAX_VALUE;

    float minf() default Float.MIN_VALUE;
    float maxf() default Float.MAX_VALUE;

    double mind() default Double.MIN_VALUE;
    double maxd() default Double.MAX_VALUE;
}
