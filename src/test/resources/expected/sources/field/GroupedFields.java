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
import sources.field.GroupedFields;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
    public static final FieldReference<Integer> abc_def_field2 = FieldReference.builder(value -> GroupedFields.field2 = value, () -> GroupedFields.field2, new GenericType(Integer.class), "FIELD2").doesSync(true).group("abc.def").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'abc.def.FIELD2' must be a number. Resetting to default value: '43'").fallback(true).defaultSupplier(() -> 43).build()).build();

    public static final FieldReference<Integer> abc_def_ghi_field3 = FieldReference.builder(value -> GroupedFields.field3 = value, () -> GroupedFields.field3, new GenericType(Integer.class), "FIELD3").doesSync(true).group("abc.def.ghi").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'abc.def.ghi.FIELD3' must be a number. Resetting to default value: '44'").fallback(true).defaultSupplier(() -> 44).build()).build();

    public static final FieldReference<Integer> abc_def_ghi_jkl_mno_field5 = FieldReference.builder(value -> GroupedFields.field5 = value, () -> GroupedFields.field5, new GenericType(Integer.class), "FIELD5").doesSync(true).group("abc.def.ghi.jkl.mno").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'abc.def.ghi.jkl.mno.FIELD5' must be a number. Resetting to default value: '46'").fallback(true).defaultSupplier(() -> 46).build()).build();

    public static final FieldReference<Integer> abc_field = FieldReference.builder(value -> GroupedFields.field = value, () -> GroupedFields.field, new GenericType(Integer.class), "FIELD").doesSync(true).group("abc").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'abc.FIELD' must be a number. Resetting to default value: '42'").fallback(true).defaultSupplier(() -> 42).build()).build();

    public static final FieldReference<Integer> abc_field4 = FieldReference.builder(value -> GroupedFields.field4 = value, () -> GroupedFields.field4, new GenericType(Integer.class), "FIELD4").doesSync(true).group("abc").validator(ValidatorReference.<Integer>builder().messageProvider(ignored -> "Value for 'abc.FIELD4' must be a number. Resetting to default value: '45'").fallback(true).defaultSupplier(() -> 45).build()).build();

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
        fields.add(abc_def_field2);
        fields.add(abc_def_ghi_field3);
        fields.add(abc_def_ghi_jkl_mno_field5);
        fields.add(abc_field);
        fields.add(abc_field4);
        fields.sort(Comparator.comparing(ref -> "%s.%s".formatted(ref.group(), ref.name())));
        return fields;
    }
}