package com.bawnorton.configurable.ap;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ap.generator.ConfigGenerator;
import com.bawnorton.configurable.ap.sourceprovider.SourceProvider;
import com.bawnorton.configurable.ap.sourceprovider.SourceProviders;
import com.bawnorton.configurable.ap.util.ConfigurableElement;
import com.bawnorton.configurable.impl.ConfigurableSettings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.bawnorton.configurable.Configurable")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class ConfigurableAP extends AbstractProcessor {
    protected final Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        types = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Configurable.class);
        if(elements.isEmpty()) return false;

        List<ConfigurableElement> roots = constructConfigurableTree(elements);
        if(roots.isEmpty()) return false;

        SourceProviders.registerDefaultSourceProviders();

        Filer filer = processingEnv.getFiler();
        SourceProvider sourceProvider = SourceProviders.getSourceProvider(filer);
        String configName = sourceProvider.getName();
        processingEnv.getMessager().printNote("Found config name: %s".formatted(configName));

        String packageName = "com.bawnorton.configurable.%s".formatted(configName.replaceAll("[^A-Za-z]", ""));
        String loaderClassName = "ConfigLoader";
        String configClassName = "Config";

        ConfigurableSettings settings = generateSettings(configName, configClassName, loaderClassName, packageName, filer);
        ConfigGenerator generator = new ConfigGenerator(filer, settings);
        try {
            generator.generateConfigLoader();
        } catch (IOException e) {
            throw new RuntimeException("Could not generate config loader", e);
        }

        StringBuilder configContent = new StringBuilder();
        Set<String> neededImports = new HashSet<>();
        roots.forEach(root -> addElement(configContent, root, neededImports, 1));
        try {
            generator.generateConfig(configContent.toString(), neededImports);
        } catch (IOException e) {
            throw new RuntimeException("Could not generate config", e);
        }

        return true;
    }

    private void addElement(StringBuilder builder, ConfigurableElement element, Set<String> neededImports, int depth) {
        if (element.childless()) {
            addReference(builder, element, neededImports, depth);
        } else {
            String container = """
            %1$spublic final %2$s %3$s = new %2$s();
            
            %1$spublic static class %2$s {
            <content>
            %1$s}
            """.formatted(
                    "\t".repeat(depth),
                    element.getNestedName(),
                    element.getKey()
            );
            StringBuilder contentBuilder = new StringBuilder();
            for (ConfigurableElement child : element.children()) {
                addElement(contentBuilder, child, neededImports, depth + 1);
            }
            container = container.replaceAll("<content>", contentBuilder.toString().replaceAll("\\r?\\n$", ""));
            builder.append(container);
        }
    }

    private void addReference(StringBuilder builder, ConfigurableElement element, Set<String> neededImports, int depth) {
        String reference = """
        public final Reference<%s> %s = new Reference<>(%s.class, \\"%s\\", %s.class);
        """.formatted(
                element.getTypeForGeneric(types),
                element.getKey(),
                element.getOwner(),
                element.getElementName(),
                element.getType()
        );
        neededImports.add(element.getFullyQualifiedType(types));
        neededImports.add(element.getFullyQualifiedOwner(types));
        builder.append("\t".repeat(depth)).append(reference).append("\n");
    }

    private List<ConfigurableElement> constructConfigurableTree(Set<? extends Element> elements) {
        Set<ConfigurableElement> rootElements = new HashSet<>();

        Set<Element> orphanedClasses = new HashSet<>();
        Set<Element> orphanedFields = new HashSet<>();
        for (Element element : elements) {
            if (!element.getKind().isClass()) {
                orphanedFields.add(element);
                continue;
            }

            Element enclosing = element.getEnclosingElement();
            if(enclosing.getKind().isClass()) {
                orphanedClasses.add(element);
                continue;
            }

            ConfigurableElement configurableElement = createConfigurableElement(element);
            rootElements.add(configurableElement);
        }

        addOrphaned(rootElements, orphanedClasses);
        addOrphaned(rootElements, orphanedFields);

        List<ConfigurableElement> sorted = new ArrayList<>(rootElements);
        sorted.sort(Comparator.comparing(ConfigurableElement::getKey));
        return sorted;
    }

    private void addOrphaned(Set<ConfigurableElement> rootElements, Set<Element> orphaned) {
        while(!orphaned.isEmpty()) {
            Set<Element> visited = rootElements.stream()
                    .flatMap(element -> {
                        List<ConfigurableElement> relevant = element.getAllChildren();
                        relevant.add(element);
                        return relevant.stream();
                    })
                    .map(ConfigurableElement::element)
                    .collect(Collectors.toSet());
            orphaned.removeAll(visited);

            for(Element orphan : orphaned) {
                ConfigurableElement configurableElement = createConfigurableElement(orphan);
                rootElements.add(configurableElement);
            }
        }
    }

    private ConfigurableElement createConfigurableElement(Element element) {
        Configurable annotation = element.getAnnotation(Configurable.class);
        if (annotation == null) {
            throw new IllegalStateException("Element \"" + element.getSimpleName() + "\" does not have Configurable annotation");
        }

        if(element.getKind().isField()) {
            Set<Modifier> modifiers = element.getModifiers();
            if(modifiers.contains(Modifier.FINAL)) {
                throw new IllegalStateException("Configurable field \"" + element.getSimpleName() + "\" cannot be final");
            } else if (!modifiers.contains(Modifier.STATIC)) {
                throw new IllegalStateException("Configurable field \"" + element.getSimpleName() + "\" must be static");
            }
        }

        List<ConfigurableElement> children = element.getEnclosedElements()
                .stream()
                .filter(e -> e.getAnnotation(Configurable.class) != null)
                .map(this::createConfigurableElement)
                .sorted(Comparator.comparing(ConfigurableElement::getKey))
                .toList();

        if (element.getKind().isClass() && children.isEmpty()) {
            throw new IllegalStateException("Configurable class \"" + element.getSimpleName() + "\" must have at least one Configurable field or class");
        }

        return new ConfigurableElement(element, annotation, children);
    }

    private ConfigurableSettings generateSettings(String configName, String configClassName, String loaderClassName, String packageName, Filer filer) {
        ConfigurableSettings settings = new ConfigurableSettings(configName, configClassName, loaderClassName, packageName);
        try {
            FileObject dummy = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "dummy.txt");
            URI location = dummy.toUri();
            Path path = Paths.get(location);
            while(!path.endsWith("build")) { // try to find the build dir
                path = path.getParent();
                if(path == null) break;
            }
            if(path != null) {
                path = path.resolve("resources/main/configurable.json");
                if(!Files.exists(path)) {
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);
                }
                OutputStream out = new FileOutputStream(path.toFile());
                out.write(gson.toJson(settings).getBytes());
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write settings file", e);
        }
        return settings;
    }
}