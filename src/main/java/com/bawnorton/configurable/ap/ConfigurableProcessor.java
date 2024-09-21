package com.bawnorton.configurable.ap;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ap.generator.ConfigGenerator;
import com.bawnorton.configurable.ap.generator.ConfigLoaderGenerator;
import com.bawnorton.configurable.ap.generator.ConfigScreenFactoryGenerator;
import com.bawnorton.configurable.ap.helper.MappingsHelper;
import com.bawnorton.configurable.ap.sourceprovider.SourceProvider;
import com.bawnorton.configurable.ap.sourceprovider.SourceProviders;
import com.bawnorton.configurable.ap.tree.ConfigurableElement;
import com.bawnorton.configurable.ap.tree.ConfigurableTree;
import com.bawnorton.configurable.load.ConfigurableSettings;
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
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.jetbrains.annotations.NotNull;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.bawnorton.configurable.Configurable")
//? if >=1.21 {
/*@SupportedSourceVersion(SourceVersion.RELEASE_21)
*///?} else {
@SupportedSourceVersion(SourceVersion.RELEASE_17)
//?}
public final class ConfigurableProcessor extends AbstractProcessor {
    private final Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private Types types;
    private Messager messager;
    private Elements elementUtils;

    private boolean yaclPresent;
    private boolean clientAccess;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        types = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();

        yaclPresent = elementUtils.getTypeElement("dev.isxander.yacl3.api.YetAnotherConfigLib") != null;
        clientAccess = elementUtils.getTypeElement(MappingsHelper.getMinecraftClient()) != null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Configurable.class);
        if(elements.isEmpty()) return false;

        ConfigurableTree tree = new ConfigurableTree(messager, elements);
        List<ConfigurableElement> roots = tree.getRoots();
        if(roots.isEmpty()) return false;

        SourceProviders.registerDefaultSourceProviders();

        Filer filer = processingEnv.getFiler();
        String sourceSet;
        Path buildPath;
        try {
            FileObject dummyClass = filer.getResource(StandardLocation.CLASS_OUTPUT, "", "dummy.class");
            Path dummyPath = Paths.get(dummyClass.toUri());
            buildPath = dummyPath.getParent();
            sourceSet = buildPath.getFileName().toString();

            if(sourceSet.equals("main")) {
                sourceSet = null;
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Cannot determine source set");
            throw new RuntimeException(e);
        }

        SourceProvider sourceProvider = SourceProviders.getSourceProvider(filer, buildPath);
        if(sourceProvider == null) {
            messager.printMessage(Diagnostic.Kind.ERROR,"Cannot determine source provider");
            throw new RuntimeException();
        }

        String configName = sourceProvider.getName();
        ConfigurableSettings settings = generateSettings(sourceSet, configName, buildPath);
        messager.printMessage(Diagnostic.Kind.NOTE, "Found config name: \"%s\" for source set \"%s\"".formatted(settings.name(), settings.sourceSet()));

        ConfigLoaderGenerator loaderGenerator = new ConfigLoaderGenerator(filer, types, messager, settings);
        ConfigGenerator configGenerator = new ConfigGenerator(filer, types, messager, settings);

        try {
            loaderGenerator.generateConfigLoader();
            configGenerator.generateConfig(roots);

            if(settings.hasScreenFactory()) {
                ConfigScreenFactoryGenerator screenFactoryGenerator = new ConfigScreenFactoryGenerator(filer, elementUtils, types, messager, settings);
                screenFactoryGenerator.generateConfigScreenFactory();
                screenFactoryGenerator.generateYaclScreenFactory(roots, configGenerator::getExternalReference);
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,"Could not generate config classes");
            throw new RuntimeException(e);
        }

        return true;
    }

    private ConfigurableSettings generateSettings(String sourceSet, String configName, Path buildPath) {
        ConfigurableSettings settings = createSettings(sourceSet, configName);
        generateSettingsFile(settings, buildPath);
        return settings;
    }

    private @NotNull ConfigurableSettings createSettings(String sourceSet, String configName) {
        String normalisedConfigName = configName.replaceAll("[^A-Za-z]", "");
        String packageName;
        if(sourceSet == null) {
            packageName = "com.bawnorton.configurable.%s".formatted(normalisedConfigName);
        } else {
            packageName = "com.bawnorton.configurable.%s.%s".formatted(sourceSet, normalisedConfigName);
        }
        return new ConfigurableSettings(
                sourceSet,
                configName,
                "Config",
                "ConfigLoader",
                yaclPresent && clientAccess ? "ConfigScreenFactory" : null,
                packageName
        );
    }

    private void generateSettingsFile(ConfigurableSettings settings, Path buildPath) {
        // try to find the build dir
        Path path = buildPath;
        while(!path.endsWith("build")) {
            path = path.getParent();
            if(path == null) {
                return;
            }
        }

        path = path.resolve("resources/%1$s/configurable/%1$s.json".formatted(settings.sourceSet()));
        try {
            if(!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            try(OutputStream out = new FileOutputStream(path.toFile())) {
                out.write(gson.toJson(settings).getBytes());
            }
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR,"Could not write settings file");
            throw new RuntimeException(e);
        }
    }
}
