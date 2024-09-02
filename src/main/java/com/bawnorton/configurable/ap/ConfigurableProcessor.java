package com.bawnorton.configurable.ap;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ap.generator.ConfigGenerator;
import com.bawnorton.configurable.ap.generator.ConfigLoaderGenerator;
import com.bawnorton.configurable.ap.generator.ConfigScreenFactoryGenerator;
import com.bawnorton.configurable.ap.sourceprovider.SourceProvider;
import com.bawnorton.configurable.ap.sourceprovider.SourceProviders;
import com.bawnorton.configurable.ap.tree.ConfigurableTree;
import com.bawnorton.configurable.ap.tree.ConfigurableElement;
import com.bawnorton.configurable.impl.ConfigurableSettings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
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
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.bawnorton.configurable.Configurable")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public final class ConfigurableProcessor extends AbstractProcessor {
    private final Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private Types types;
    private Messager messager;
    private Elements elementUtils;
    private boolean yaclPresent;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        types = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        yaclPresent = elementUtils.getTypeElement("dev.isxander.yacl3.api.YetAnotherConfigLib") != null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Configurable.class);
        if(elements.isEmpty()) return false;

        ConfigurableTree tree = new ConfigurableTree(messager, elementUtils, elements);
        List<ConfigurableElement> roots = tree.getRoots();
        if(roots.isEmpty()) return false;

        SourceProviders.registerDefaultSourceProviders();

        Filer filer = processingEnv.getFiler();
        SourceProvider sourceProvider = SourceProviders.getSourceProvider(filer);
        String configName = sourceProvider.getName();
        messager.printNote("Found config name: %s".formatted(configName));

        String packageName = "com.bawnorton.configurable.%s".formatted(configName.replaceAll("[^A-Za-z]", ""));

        ConfigurableSettings settings = generateSettings(configName, packageName, filer);

        ConfigLoaderGenerator loaderGenerator = new ConfigLoaderGenerator(filer, types, messager, settings);
        ConfigGenerator configGenerator = new ConfigGenerator(filer, types, messager, settings);

        try {
            loaderGenerator.generateConfigLoader();
            configGenerator.generateConfig(roots);
        } catch (IOException e) {
            throw new RuntimeException("Could not generate config classes", e);
        }

        if(yaclPresent) {
            ConfigScreenFactoryGenerator screenFactoryGenerator = new ConfigScreenFactoryGenerator(filer, elementUtils, types, messager, settings);
            try {
                screenFactoryGenerator.generateConfigScreenFactory();
                screenFactoryGenerator.generateYaclScreenFactory(roots, configGenerator::getExternalReference);
            } catch (IOException e) {
                throw new RuntimeException("Could not generate config screen factory", e);
            }
        }

        return true;
    }

    private ConfigurableSettings generateSettings(String configName, String packageName, Filer filer) {
        ConfigurableSettings settings = new ConfigurableSettings(
                configName,
                "Config",
                "ConfigLoader",
                yaclPresent ? "ConfigScreenFactory" : null,
                packageName
        );
        try {
            // try to find the build dir
            FileObject dummy = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "dummy.txt");
            URI location = dummy.toUri();
            Path path = Paths.get(location);
            while(!path.endsWith("build")) {
                path = path.getParent();
                if(path == null) {
                    return settings;
                }
            }

            path = path.resolve("resources/main/configurable.json");
            if(!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            OutputStream out = new FileOutputStream(path.toFile());
            out.write(gson.toJson(settings).getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write settings file", e);
        }
        return settings;
    }
}