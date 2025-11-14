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

import sources.validator.CustomValidator;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
	public static final FieldReference<Integer> field = FieldReference.builder(value -> CustomValidator.field = value, () -> CustomValidator.field, new GenericType(Integer.class), "FIELD")
			.doesSync(true)
			.validator(ValidatorReference.<Integer>builder()
					.fieldValidator(CustomValidator::simpleValidator)
					.messageProvider(ignored -> "Value for 'FIELD' does not adhere to its validator: 'simpleValidator'. Resetting to default value: '42'")
					.fallback(true)
					.defaultSupplier(() -> 42)
					.build())
			.build();

	public static final FieldReference<Integer> fieldWithInteger = FieldReference.builder(value -> CustomValidator.fieldWithInteger = value, () -> CustomValidator.fieldWithInteger, new GenericType(Integer.class), "FIELD_WITH_INTEGER")
			.doesSync(true)
			.validator(ValidatorReference.<Integer>builder()
					.fieldValidator(CustomValidator::simpleValidator)
					.messageProvider(ignored -> "Value for 'FIELD_WITH_INTEGER' does not adhere to its validator: 'simpleValidator'. Resetting to default value: '42'")
					.fallback(true)
					.defaultSupplier(() -> 42)
					.build())
			.build();

	public static final FieldReference<Integer> fieldWithMax = FieldReference.builder(value -> CustomValidator.fieldWithMax = value, () -> CustomValidator.fieldWithMax, new GenericType(Integer.class), "FIELD_WITH_MAX")
			.doesSync(true)
			.validator(ValidatorReference.<Integer>builder()
					.fieldValidator(value -> value <= 100.0)
					.messageProvider(ignored -> "Value for 'FIELD_WITH_MAX' must be less than or equal to '100.0'. Resetting to default value: '42'")
					.fallback(true)
					.defaultSupplier(() -> 42)
					.build())
			.build();

	public static final FieldReference<Integer> fieldWithMin = FieldReference.builder(value -> CustomValidator.fieldWithMin = value, () -> CustomValidator.fieldWithMin, new GenericType(Integer.class), "FIELD_WITH_MIN")
			.doesSync(true)
			.validator(ValidatorReference.<Integer>builder()
					.fieldValidator(value -> value >= 0.0)
					.messageProvider(ignored -> "Value for 'FIELD_WITH_MIN' must be greater than or equal to '0.0'. Resetting to default value: '42'")
					.fallback(true)
					.defaultSupplier(() -> 42)
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
		fields.add(field);
		fields.add(fieldWithInteger);
		fields.add(fieldWithMax);
		fields.add(fieldWithMin);
		fields.sort(Comparator.comparing(ref -> "%s.%s".formatted(ref.group(), ref.name())));
		return fields;
	}
}