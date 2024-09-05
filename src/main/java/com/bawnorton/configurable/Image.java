package com.bawnorton.configurable;

public @interface Image {
    ImageType type() default ImageType.RESOURCE;

    String id() default "";

    String path() default "";

    String custom() default "";

    int width() default 16;

    int height() default 16;

    float u() default 0;

    float v() default 0;

    int textureWidth() default 0;

    int textureHeight() default 0;
}
