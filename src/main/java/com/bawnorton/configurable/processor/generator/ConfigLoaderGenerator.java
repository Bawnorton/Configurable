package com.bawnorton.configurable.processor.generator;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.processor.ConfigurableSettings;
import com.bawnorton.configurable.processor.entry.ConfigurableEntry;
import com.bawnorton.configurable.processor.entry.ConfigurableValidator;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericHolder;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
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
        genericHolderBuilder.add("new $T(", GenericHolder.class);
        if(fieldType instanceof DeclaredType declaredType) {
            genericHolderBuilder.add("$T.class", processingEnv.getTypeUtils().erasure(declaredType));
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (!typeArguments.isEmpty()) {
                for (TypeMirror typeArgument : typeArguments) {
                    genericHolderBuilder.add(", $T.class", typeArgument);
                }
            }
        } else {
            genericHolderBuilder.add("$T.class", fieldType);
        }
        genericHolderBuilder.add(")");
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

        ConfigurableValidator validator = entry.getValidator();
        CodeBlock.Builder validatorBuilder = CodeBlock.builder();
        validatorBuilder.add(
                "$T.<$T>builder()",
                ValidatorReference.class,
                fieldType
        );
        if (validator.hasValidatorMethod()) {
            validatorBuilder.add(
                    ".fieldValidator($T::$L)",
                    enclosingClass,
                    validator.getValidatorMethod().getName()
            );
        }
        if (validator.hasMessageMethod()) {
            validatorBuilder.add(
                    ".messageProvider($T::$L)",
                    enclosingClass,
                    validator.getMessageMethod().getName()
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

    public JavaFile generate() {
        MethodSpec.Builder loadBuilder = MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class);

        MethodSpec.Builder saveBuilder = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class);

        fields.sort(Comparator.comparing(FieldSpec::name));
        for (FieldSpec field : fields) {
            typeSpecBuilder.addField(field);

            loadBuilder.addStatement("$L.load()", field.name());
            saveBuilder.addStatement("$L.save()", field.name());
        }

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

        typeSpecBuilder.addMethod(loadBuilder.build());
        typeSpecBuilder.addMethod(saveBuilder.build());

        TypeSpec typeSpec = typeSpecBuilder.build();
        return JavaFile.builder(packageName, typeSpec)
                       .skipJavaLangImports(true)
                       .build();
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }
}
