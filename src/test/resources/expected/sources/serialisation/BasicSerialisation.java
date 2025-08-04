package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericType;
import com.google.auto.service.AutoService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.processing.Generated;
import sources.serialisation.BasicSerialisation;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Boolean> booleanField = FieldReference.builder(value -> BasicSerialisation.booleanField = value, () -> BasicSerialisation.booleanField, new GenericType(Boolean.class), "BOOLEAN_FIELD").doesSync(true).validator(ValidatorReference.<Boolean>builder().messageProvider(ignored -> "Value for 'BOOLEAN_FIELD' is invalid").build()).build();

    public static final FieldReference<Byte> byteField = FieldReference.builder(value -> BasicSerialisation.byteField = value, () -> BasicSerialisation.byteField, new GenericType(Byte.class), "BYTE_FIELD").doesSync(true).validator(ValidatorReference.<Byte>builder().messageProvider(ignored -> "Value for 'BYTE_FIELD' must be a number").build()).build();

    public static final FieldReference<Character> charField = FieldReference.builder(value -> BasicSerialisation.charField = value, () -> BasicSerialisation.charField, new GenericType(Character.class), "CHAR_FIELD").doesSync(true).validator(ValidatorReference.<Character>builder().messageProvider(ignored -> "Value for 'CHAR_FIELD' is invalid").build()).build();

    public static final FieldReference<Integer> customField = FieldReference.builder(value -> BasicSerialisation.customField = value, () -> BasicSerialisation.customField, new GenericType(Integer.class), "CUSTOM_NAME").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'CUSTOM_NAME' must be a number").build()).build();

    public static final FieldReference<Double> doubleField = FieldReference.builder(value -> BasicSerialisation.doubleField = value, () -> BasicSerialisation.doubleField, new GenericType(Double.class), "DOUBLE_FIELD").doesSync(true).validator(ValidatorReference.<Double>builder().messageProvider(ignored -> "Value for 'DOUBLE_FIELD' must be a number").build()).build();

    public static final FieldReference<Integer> field = FieldReference.builder(value -> BasicSerialisation.field = value, () -> BasicSerialisation.field, new GenericType(Integer.class), "FIELD").doesSync(true).comment("A simple field comment").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'FIELD' must be a number").build()).build();

    public static final FieldReference<Float> floatField = FieldReference.builder(value -> BasicSerialisation.floatField = value, () -> BasicSerialisation.floatField, new GenericType(Float.class), "FLOAT_FIELD").doesSync(true).validator(ValidatorReference.<Float>builder().messageProvider(ignored -> "Value for 'FLOAT_FIELD' must be a number").build()).build();

    public static final FieldReference<int[]> intArrayField = FieldReference.builder(value -> BasicSerialisation.intArrayField = value, () -> BasicSerialisation.intArrayField, new GenericType(int[].class), "INT_ARRAY_FIELD").doesSync(true).validator(ValidatorReference.<int[]>builder().messageProvider(ignored -> "Value for 'INT_ARRAY_FIELD' is invalid").build()).build();

    public static final FieldReference<List<Integer>> integerListField = FieldReference.builder(value -> BasicSerialisation.integerListField = value, () -> BasicSerialisation.integerListField, new GenericType(List.class, new GenericType(Integer.class)), "INTEGER_LIST_FIELD").doesSync(true).validator(ValidatorReference.<List<Integer>>builder().messageProvider(ignored -> "Value for 'INTEGER_LIST_FIELD' is invalid").build()).build();

    public static final FieldReference<List<String[]>> listOfStringArraysField = FieldReference.builder(value -> BasicSerialisation.listOfStringArraysField = value, () -> BasicSerialisation.listOfStringArraysField, new GenericType(List.class, new GenericType(String[].class)), "LIST_OF_STRING_ARRAYS_FIELD").doesSync(true).validator(ValidatorReference.<List<String[]>>builder().messageProvider(ignored -> "Value for 'LIST_OF_STRING_ARRAYS_FIELD' is invalid").build()).build();

    public static final FieldReference<Long> longField = FieldReference.builder(value -> BasicSerialisation.longField = value, () -> BasicSerialisation.longField, new GenericType(Long.class), "LONG_FIELD").doesSync(true).validator(ValidatorReference.<Long>builder().messageProvider(ignored -> "Value for 'LONG_FIELD' must be a number").build()).build();

    public static final FieldReference<List<List<Integer>>> nestedListField = FieldReference.builder(value -> BasicSerialisation.nestedListField = value, () -> BasicSerialisation.nestedListField, new GenericType(List.class, new GenericType(List.class, new GenericType(Integer.class))), "NESTED_LIST_FIELD").doesSync(true).validator(ValidatorReference.<List<List<Integer>>>builder().messageProvider(ignored -> "Value for 'NESTED_LIST_FIELD' is invalid").build()).build();

    public static final FieldReference<Integer> nullableField = FieldReference.builder(value -> BasicSerialisation.nullableField = value, () -> BasicSerialisation.nullableField, new GenericType(Integer.class), "NULLABLE_FIELD").doesSync(true).validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'NULLABLE_FIELD' must be a number. Resetting to default value: 'null'").fallback(true).defaultSupplier(() -> null).build()).build();

    public static final FieldReference<Short> shortField = FieldReference.builder(value -> BasicSerialisation.shortField = value, () -> BasicSerialisation.shortField, new GenericType(Short.class), "SHORT_FIELD").doesSync(true).validator(ValidatorReference.<Short>builder().messageProvider(ignored -> "Value for 'SHORT_FIELD' must be a number").build()).build();

    public static final FieldReference<String[]> stringArrayField = FieldReference.builder(value -> BasicSerialisation.stringArrayField = value, () -> BasicSerialisation.stringArrayField, new GenericType(String[].class), "STRING_ARRAY_FIELD").doesSync(true).validator(ValidatorReference.<String[]>builder().messageProvider(ignored -> "Value for 'STRING_ARRAY_FIELD' is invalid").build()).build();

    public static final FieldReference<String> stringField = FieldReference.builder(value -> BasicSerialisation.stringField = value, () -> BasicSerialisation.stringField, new GenericType(String.class), "STRING_FIELD").doesSync(true).validator(ValidatorReference.<String>builder().messageProvider(ignored -> "Value for 'STRING_FIELD' is invalid").build()).build();

    public static final FieldReference<List<String>> stringListField = FieldReference.builder(value -> BasicSerialisation.stringListField = value, () -> BasicSerialisation.stringListField, new GenericType(List.class, new GenericType(String.class)), "STRING_LIST_FIELD").doesSync(true).validator(ValidatorReference.<List<String>>builder().messageProvider(ignored -> "Value for 'STRING_LIST_FIELD' is invalid").build()).build();

    @Override
    public String getName() {
        return "test-project";
    }

    @Override
    public FileType getFileType() {
        return FileType.TOML;
    }

    @Override
    public void load(SaveLoader saveLoader) {
        saveLoader.load(getFields());
    }

    @Override
    public void save(SaveLoader saveLoader) {
        saveLoader.save(getFields());
    }

    @Override
    public List<FieldReference<?>> getFields() {
        List<FieldReference<?>> fields = new ArrayList<>();
        fields.add(booleanField);
        fields.add(byteField);
        fields.add(charField);
        fields.add(customField);
        fields.add(doubleField);
        fields.add(field);
        fields.add(floatField);
        fields.add(intArrayField);
        fields.add(integerListField);
        fields.add(listOfStringArraysField);
        fields.add(longField);
        fields.add(nestedListField);
        fields.add(nullableField);
        fields.add(shortField);
        fields.add(stringArrayField);
        fields.add(stringField);
        fields.add(stringListField);
        fields.sort(Comparator.comparing(ref -> "%s.%s".formatted(ref.group(), ref.name())));
        return fields;
    }
}