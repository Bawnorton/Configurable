package com.bawnorton.configurable.generated.test_project;

import com.bawnorton.configurable.bootstrap.ServiceLoadedValue;
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
import sources.serialisation.CustomTypeCompileSerialisation;

/**
 * Generated config loader for "test-project".
 */
@Generated("com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator")
@AutoService(ConfigLoader.class)
public final class GeneratedConfigLoader implements ConfigLoader {
  public static final FieldReference<ServiceLoadedValue> customValue = FieldReference.builder(value -> CustomTypeCompileSerialisation.customValue = value, () -> CustomTypeCompileSerialisation.customValue, new GenericType(ServiceLoadedValue.class), "CUSTOM_VALUE").doesSync(true).validator(ValidatorReference.<ServiceLoadedValue>builder().messageProvider(ignored -> "Value for 'CUSTOM_VALUE' is invalid").build()).build();

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
    fields.add(customValue);
    fields.sort(Comparator.comparing(ref -> "%s.%s".formatted(ref.group(), ref.name())));
    return fields;
  }
}


