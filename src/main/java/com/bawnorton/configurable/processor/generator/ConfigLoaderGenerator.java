package com.bawnorton.configurable.processor.generator;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.processor.ConfigurableSettings;
import com.bawnorton.configurable.processor.entry.ConfigurableEntry;
import com.bawnorton.configurable.processor.entry.ConfigurableValidator;
import com.bawnorton.configurable.processor.util.MethodReference;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericType;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.*;
import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConfigLoaderGenerator {
    private final ProcessingEnvironment processingEnv;
    private final TypeSpec.Builder typeSpecBuilder;
    private final String configName;
    private final String packageName;
    private final FileType fileType;

    private final List<FieldSpec> fields = new ArrayList<>();

    public ConfigLoaderGenerator(ProcessingEnvironment processingEnv, ConfigurableSettings settings) {
        this.processingEnv = processingEnv;
        this.configName = settings.name();
        this.fileType = settings.fileType();
        this.packageName = "com.bawnorton.configurable.generated.%s".formatted(formatForPackage(settings.name()));
        this.typeSpecBuilder = TypeSpec.classBuilder("GeneratedConfigLoader")
                .addSuperinterface(ConfigLoader.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "$S", ConfigLoaderGenerator.class.getCanonicalName())
                        .build())
                .addAnnotation(AnnotationSpec.builder(AutoService.class)
                        .addMember("value", "$T.class", ConfigLoader.class)
                        .build())
                .addJavadoc("Generated config loader for $S.", settings.name());
    }

    private static String formatForPackage(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }

    public void addEntry(ConfigurableEntry entry) {
        TypeMirror fieldType = entry.getTypeMirror(processingEnv);
        TypeName fieldReferenceType = TypeName.get(processingEnv.getTypeUtils().getDeclaredType(
                processingEnv.getElementUtils().getTypeElement(FieldReference.class.getCanonicalName()),
                fieldType
        ));
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(
                fieldReferenceType,
                entry.getReferenceName(),
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL
        );
        CodeBlock.Builder initalizerBuilder = CodeBlock.builder();
        TypeName enclosingClass = TypeName.get(entry.getEnclosingClassTypeMirror());
        CodeBlock.Builder genericHolderBuilder = CodeBlock.builder();
        writeGenericHolder(fieldType, genericHolderBuilder);
        initalizerBuilder.add(
                "$1T.builder(value -> $2T.$3L = value, () -> $2T.$3L, $4L, $5S)",
                FieldReference.class,
                enclosingClass,
                entry.getFieldName(),
                genericHolderBuilder.build(),
                entry.getName()
        );
        if (entry.doesSync()) {
            initalizerBuilder.add(".doesSync(true)");
        }
        if (entry.hasComment()) {
            initalizerBuilder.add(".comment($S)", entry.getComment());
        }
        if (entry.hasGroup()) {
            initalizerBuilder.add(".group($S)", entry.getGroup());
        }
        if (entry.hasOnSetMethod()) {
            MethodReference onSetMethod = entry.getOnSetMethod();
            initalizerBuilder.add(".onSet($T::$L)",
                    onSetMethod.enclosingClass(),
                    onSetMethod.getName()
            );
        }

        ConfigurableValidator validator = entry.getValidator();
        CodeBlock.Builder validatorBuilder = CodeBlock.builder();
        validatorBuilder.add(
                "$T.<$T>builder()",
                ValidatorReference.class,
                fieldType
        );
        if (validator.hasValidatorMethod()) {
            MethodReference validatorMethod = validator.getValidatorMethod();
            validatorBuilder.add(
                    ".fieldValidator($T::$L)",
                    validatorMethod.enclosingClass(),
                    validatorMethod.getName()
            );
        } else if (validator.hasMax() && validator.hasMin()) {
            validatorBuilder.add(
                    ".fieldValidator(value -> value <= $L && value >= $L)",
                    validator.getMax(),
                    validator.getMin()
            );
        } else if (validator.hasMax()) {
            validatorBuilder.add(
                    ".fieldValidator(value -> value <= $L)",
                    validator.getMax()
            );
        } else if (validator.hasMin()) {
            validatorBuilder.add(
                    ".fieldValidator(value -> value >= $L)",
                    validator.getMin()
            );
        }
        if (validator.hasMessageMethod()) {
            MethodReference messageMethod = validator.getMessageMethod();
            validatorBuilder.add(
                    ".messageProvider($T::$L)",
                    messageMethod.enclosingClass(),
                    messageMethod.getName()
            );
        } else {
            validatorBuilder.add(
                    ".messageProvider(ignored -> $S)",
                    validator.getMessageLiteral()
            );
        }
        if (validator.doesFallback()) {
            validatorBuilder.add(".fallback(true)");
            validatorBuilder.add(
                    ".defaultSupplier(() -> $L)",
                    validator.getDefaultValue()
            );
        }
        validatorBuilder.add(".build()");
        initalizerBuilder.add(".validator($L)", validatorBuilder.build());
        initalizerBuilder.add(".build()");
        fieldBuilder.initializer(initalizerBuilder.build());
        FieldSpec field = fieldBuilder.build();
        fields.add(field);
    }

    private void writeGenericHolder(TypeMirror type, CodeBlock.Builder builder) {
        builder.add("new $T(", GenericType.class);

        if (type instanceof DeclaredType declaredType) {
            builder.add("$T.class", processingEnv.getTypeUtils().erasure(declaredType));

            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (!typeArguments.isEmpty()) {
                for (TypeMirror typeArgument : typeArguments) {
                    builder.add(", ");
                    writeGenericHolder(typeArgument, builder);
                }
            }
            builder.add(")");
        } else {
            builder.add("$T.class)", type);
        }
    }

    public JavaFile generate() {
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("getName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addAnnotation(Override.class)
                .addStatement("return $S", configName)
                .build());

        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("getFileType")
                .addModifiers(Modifier.PUBLIC)
                .returns(FileType.class)
                .addAnnotation(Override.class)
                .addStatement("return $T.$L", FileType.class, fileType.name())
                .build());

        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(SaveLoader.class, "saveLoader")
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("saveLoader.load(getFields())")
                .build());

        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(SaveLoader.class, "saveLoader")
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("saveLoader.save(getFields())")
                .build());

        MethodSpec.Builder getFieldsBuilder = MethodSpec.methodBuilder("getFields")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(List.class),
                        ParameterizedTypeName.get(
                                ClassName.get(FieldReference.class),
                                WildcardTypeName.subtypeOf(Object.class)
                        )
                ))
                .addAnnotation(Override.class);

        getFieldsBuilder.addStatement(
                "$T<$T<$T>> fields = new $T<>()",
                List.class,
                FieldReference.class,
                WildcardTypeName.subtypeOf(Object.class),
                ArrayList.class
        );

        fields.sort(Comparator.comparing(FieldSpec::name));
        for (FieldSpec field : fields) {
            typeSpecBuilder.addField(field);

            getFieldsBuilder.addStatement("fields.add($L)", field.name());
        }

        getFieldsBuilder.addStatement("fields.sort($T.comparing(ref -> $S.formatted(ref.group(), ref.name())))",
                Comparator.class,
                "%s.%s"
        );
        getFieldsBuilder.addStatement("return fields");

        typeSpecBuilder.addMethod(getFieldsBuilder.build());

        TypeSpec typeSpec = typeSpecBuilder.build();
        return JavaFile.builder(packageName, typeSpec)
                .skipJavaLangImports(true)
                .build();
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }
}
