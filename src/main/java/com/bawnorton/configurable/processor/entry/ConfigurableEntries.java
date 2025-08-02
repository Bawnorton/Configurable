package com.bawnorton.configurable.processor.entry;

import com.bawnorton.configurable.processor.ConfigurableSettings;
import com.bawnorton.configurable.processor.element.ConfigurableElement;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class ConfigurableEntries implements Iterable<ConfigurableEntry> {
    private final Set<ConfigurableEntry> entries;

    private ConfigurableEntries(Set<ConfigurableEntry> entries) {
        this.entries = entries;
    }

    public static ConfigurableEntries fromElements(Set<? extends Element> elements, ConfigurableSettings settings, ProcessingEnvironment processingEnv) {
        Set<ConfigurableEntry> entries = new HashSet<>();
        Set<String> existingNames = new HashSet<>();
        for(Element element : elements) {
            ConfigurableElement configurableElement = ConfigurableElement.fromElement(element, processingEnv);
            if (configurableElement == null) continue;

            String configEntryName = configurableElement.getConfigurableName(settings);
            if (!existingNames.add(configEntryName)) {
                processingEnv.getMessager().printError("Duplicate configurable entry name '%s' found in class '%s'. Each entry must have a unique name.".formatted(configEntryName, element.getEnclosingElement().getSimpleName()), element);
                continue;
            }

            ConfigurableEntry entry = ConfigurableEntry.createEntry(configEntryName, configurableElement, processingEnv);
            if (entry == null) continue;

            entries.add(entry);
        }
        return new ConfigurableEntries(entries);
    }

    @Override
    public @NotNull Iterator<ConfigurableEntry> iterator() {
        return entries.iterator();
    }
}
