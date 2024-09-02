package com.bawnorton.configurable.ap.generator;

import com.bawnorton.configurable.impl.ConfigurableSettings;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;

public abstract class ConfigurableGenerator {
    protected final Filer filer;
    protected final Types types;
    protected final Messager messager;
    protected final ConfigurableSettings settings;

    protected ConfigurableGenerator(Filer filer, Types types, Messager messager, ConfigurableSettings settings) {
        this.filer = filer;
        this.types = types;
        this.messager = messager;
        this.settings = settings;
    }

    protected String applyReplacements(String spec) {
        spec = spec.replaceAll("<name>", settings.name());
        spec = spec.replaceAll("<configurable_package>", settings.packageName());
        spec = spec.replaceAll("<config_class_name>", settings.fullyQualifiedConfig());
        return spec;
    }
}
