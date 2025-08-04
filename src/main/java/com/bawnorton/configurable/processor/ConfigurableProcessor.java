package com.bawnorton.configurable.processor;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.processor.entry.ConfigurableEntries;
import com.bawnorton.configurable.processor.entry.ConfigurableEntry;
import com.bawnorton.configurable.processor.generator.ConfigLoaderGenerator;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.JavaFile;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@SupportedAnnotationTypes("com.bawnorton.configurable.Configurable")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ConfigurableProcessor extends AbstractProcessor {
    private ConfigurableSettings settings;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Configurable.class);
        if (elements.isEmpty()) return false;

        loadSettings(processingEnv);
        if (settings == null) return true;

        ConfigurableEntries entries = ConfigurableEntries.fromElements(elements, settings, processingEnv);
        if (entries == null) return true;

        ConfigLoaderGenerator generator = new ConfigLoaderGenerator(processingEnv, settings);
        for(ConfigurableEntry entry : entries) {
            generator.addEntry(entry);
        }
        if (generator.isEmpty()) return true;

        try {
            JavaFile generated = generator.generate();
            generated.writeTo(processingEnv.getFiler());
            processingEnv.getMessager().printNote("\n" + generated);
        } catch (IOException e) {
            processingEnv.getMessager().printError("Failed to generate config loader:\n%s: %s".formatted(
                    e.getClass().getCanonicalName(),
                    e.getMessage()
            ));
        }

        return true;
    }

    private void loadSettings(ProcessingEnvironment processingEnv) {
        if (settings != null) return;

        Filer filer = processingEnv.getFiler();
        Messager messager = processingEnv.getMessager();
        try (InputStream configStream = filer.getResource(StandardLocation.CLASS_PATH, "", "META-INF/configurable.properties").openInputStream()) {
            Properties properties = new Properties();
            properties.load(configStream);
            try {
                settings = ConfigurableSettings.fromProperties(properties);
            } catch (IllegalArgumentException e) {
                messager.printError("Invalid configurable.properties: %s".formatted(e.getMessage()));
            }
        } catch (IOException e) {
            messager.printError("Failed to read configurable.properties:\n%s: %s".formatted(
                    e.getClass().getCanonicalName(),
                    e.getMessage()
            ));
        }
    }
}
