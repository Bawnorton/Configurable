package com.bawnorton.configurable;

import dev.isxander.yacl3.api.OptionDescription;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Image {
    /**
     * Either {@link ImageType#RESOURCE} or {@link ImageType#WEBP}. For whether to use
     * {@link OptionDescription.Builder#image} or {@link OptionDescription.Builder#webpImage}<br>
     * If custom is defined, this is ignored
     */
    ImageType type() default ImageType.RESOURCE;

    /**
     * The image id. Eg {@code "minecraft:textures/item/emerald.png"}
     */
    String value() default "";

    /**
     * The absolute path of the texture
     */
    String path() default "";

    /**
     * Sets the method to use when rendering a custom image for the option/option group.<br>
     * Requirement differs when annotating a field or class:<br>
     * <br>
     * <b>Field</b><br>
     * Name of a method with either of the following signatures:
     * <pre>
     * {@code public static CompletableFuture<Optional<ImageRenderer>> name(FieldType value)}
     * {@code public static ImageRenderer name(FieldType value)}
     * </pre>
     * <br>
     * <b>Class</b><br>
     * Name of a method with either of the following signatures:
     * <pre>
     * {@code public static CompletableFuture<Optional<ImageRenderer>> name()}
     * {@code public static ImageRenderer name()}
     * </pre>
     * Any other signature will result in a compile time exception.<br>
     * Also accepts referencing a method outside of the current class:<br>
     * <pre>
     * {@code fully.qualified.owner#methodName}
     * </pre>
     */
    String custom() default "";

    /**
     * The image width. If using an atlas, this will be the width of the atlas
     */
    int width() default 16;

    /**
     * The image height. If using an atlas, this will be the height of the atlas
     */
    int height() default 16;

    /**
     * Normalised (0-1)
     */
    float u() default 0;

    /**
     * Normalised (0-1)
     */
    float v() default 0;

    /**
     * The texture width. When set to 0, will default to {@code width}
     */
    int textureWidth() default 0;

    /**
     * The texture height. When set to 0, will default to {@code height}
     */
    int textureHeight() default 0;
}
