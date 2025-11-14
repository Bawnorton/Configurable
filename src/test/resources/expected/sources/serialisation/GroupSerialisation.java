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

import sources.serialisation.GroupSerialisation;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
	public static final FieldReference<Boolean> abc_booleanValue = FieldReference.builder(value -> GroupSerialisation.booleanValue = value, () -> GroupSerialisation.booleanValue, new GenericType(Boolean.class), "BOOLEAN_VALUE")
			.doesSync(true)
			.group("abc")
			.validator(ValidatorReference.<Boolean>builder()
					.messageProvider(ignored -> "Value for 'abc.BOOLEAN_VALUE' is invalid. Resetting to default value: 'true'")
					.fallback(true)
					.defaultSupplier(() -> true)
					.build())
			.build();

	public static final FieldReference<Integer> abc_intValue = FieldReference.builder(value -> GroupSerialisation.intValue = value, () -> GroupSerialisation.intValue, new GenericType(Integer.class), "INT_VALUE")
			.doesSync(true)
			.group("abc")
			.validator(ValidatorReference.<Integer>builder()
					.messageProvider(ignored -> "Value for 'abc.INT_VALUE' must be a number. Resetting to default value: '1'")
					.fallback(true)
					.defaultSupplier(() -> 1)
					.build())
			.build();

	public static final FieldReference<String> abc_nested_deep_value_deepNestedStringValue = FieldReference.builder(value -> GroupSerialisation.deepNestedStringValue = value, () -> GroupSerialisation.deepNestedStringValue, new GenericType(String.class), "DEEP_NESTED_STRING_VALUE")
			.doesSync(true)
			.group("abc.nested.deep.value")
			.validator(ValidatorReference.<String>builder()
					.messageProvider(ignored -> "Value for 'abc.nested.deep.value.DEEP_NESTED_STRING_VALUE' is invalid. Resetting to default value: '\"deep nested test\"'")
					.fallback(true)
					.defaultSupplier(() -> "deep nested test")
					.build())
			.build();

	public static final FieldReference<Integer> abc_nested_nestedIntValue = FieldReference.builder(value -> GroupSerialisation.nestedIntValue = value, () -> GroupSerialisation.nestedIntValue, new GenericType(Integer.class), "NESTED_INT_VALUE")
			.doesSync(true)
			.group("abc.nested")
			.validator(ValidatorReference.<Integer>builder()
					.messageProvider(ignored -> "Value for 'abc.nested.NESTED_INT_VALUE' must be a number. Resetting to default value: '42'")
					.fallback(true)
					.defaultSupplier(() -> 42)
					.build())
			.build();

	public static final FieldReference<String> abc_nested_nestedStringValue = FieldReference.builder(value -> GroupSerialisation.nestedStringValue = value, () -> GroupSerialisation.nestedStringValue, new GenericType(String.class), "NESTED_STRING_VALUE")
			.doesSync(true)
			.group("abc.nested")
			.validator(ValidatorReference.<String>builder()
					.messageProvider(ignored -> "Value for 'abc.nested.NESTED_STRING_VALUE' is invalid. Resetting to default value: '\"nested test\"'")
					.fallback(true)
					.defaultSupplier(() -> "nested test")
					.build())
			.build();

	public static final FieldReference<String> abc_stringValue = FieldReference.builder(value -> GroupSerialisation.stringValue = value, () -> GroupSerialisation.stringValue, new GenericType(String.class), "STRING_VALUE")
			.doesSync(true)
			.group("abc")
			.validator(ValidatorReference.<String>builder()
					.messageProvider(ignored -> "Value for 'abc.STRING_VALUE' is invalid. Resetting to default value: '\"test\"'")
					.fallback(true)
					.defaultSupplier(() -> "test")
					.build())
			.build();

	public static final FieldReference<String> anotherStringValue = FieldReference.builder(value -> GroupSerialisation.anotherStringValue = value, () -> GroupSerialisation.anotherStringValue, new GenericType(String.class), "ANOTHER_STRING_VALUE")
			.doesSync(true)
			.validator(ValidatorReference.<String>builder()
					.messageProvider(ignored -> "Value for 'ANOTHER_STRING_VALUE' is invalid. Resetting to default value: '\"another test\"'")
					.fallback(true)
					.defaultSupplier(() -> "another test")
					.build())
			.build();

	public static final FieldReference<Double> doubleValue = FieldReference.builder(value -> GroupSerialisation.doubleValue = value, () -> GroupSerialisation.doubleValue, new GenericType(Double.class), "DOUBLE_VALUE")
			.doesSync(true)
			.validator(ValidatorReference.<Double>builder()
					.messageProvider(ignored -> "Value for 'DOUBLE_VALUE' must be a number. Resetting to default value: '3.14'")
					.fallback(true)
					.defaultSupplier(() -> 3.14)
					.build())
			.build();

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
		fields.add(abc_booleanValue);
		fields.add(abc_intValue);
		fields.add(abc_nested_deep_value_deepNestedStringValue);
		fields.add(abc_nested_nestedIntValue);
		fields.add(abc_nested_nestedStringValue);
		fields.add(abc_stringValue);
		fields.add(anotherStringValue);
		fields.add(doubleValue);
		fields.sort(Comparator.comparing(ref -> "%s.%s".formatted(ref.group(), ref.name())));
		return fields;
	}
}