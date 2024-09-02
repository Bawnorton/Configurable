package com.bawnorton.configurable.ap.generator;

import com.bawnorton.configurable.ap.tree.ConfigurableElement;
import com.bawnorton.configurable.ap.yacl.*;
import com.bawnorton.configurable.impl.ConfigurableSettings;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ConfigScreenFactoryGenerator extends ConfigurableGenerator {
    //language=Java
    private static final String SCREEN_FACTORY_SPEC = """

package <configurable_package>.client;

import <config_class_name>;
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.impl.generated.GeneratedConfigScreenFactory;
import com.bawnorton.configurable.platform.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import java.net.URI;

public final class ConfigScreenFactory implements GeneratedConfigScreenFactory {
    @Override
    public Screen createScreen(MinecraftClient client, Screen parent) {
        if(!Platform.isModLoaded("yet_another_config_lib_v3")) {
            return new ConfirmScreen(result -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
                }
                client.setScreen(parent);
            }, Text.translatable("configurable.yacl.not_installed"), Text.translatable("configurable.yacl.not_installed.message"), ScreenTexts.YES, ScreenTexts.NO);
        }
        return createYaclScreen(parent);
    }
    
    private Screen createYaclScreen(Screen parent) {
        Config config = (Config) ConfigurableMain.getWrapper("<name>").getConfig();
        return YaclScreenFactory.create(parent, config);
    }
}

""";

    //language=Java
    private static final String YACL_SCREEN_FACTORY_SPEC = """

package <configurable_package>.client;

import <config_class_name>;
import net.minecraft.client.gui.screen.Screen;
<imports>

public final class YaclScreenFactory {
    public static Screen create(Screen parent, Config config) {
        return <yacl>
    }
}
""";

    private final Elements elements;

    public ConfigScreenFactoryGenerator(Filer filer, Elements elements, Types types, Messager messager, ConfigurableSettings settings) {
        super(filer, types, messager, settings);
        this.elements = elements;
    }

    public void generateConfigScreenFactory() throws IOException {
        String spec = SCREEN_FACTORY_SPEC;
        spec = applyReplacements(spec);
        JavaFileObject configLoader = filer.createSourceFile(settings.fullyQualifiedScreenFactory());
        try (PrintWriter out = new PrintWriter(configLoader.openWriter())) {
            out.println(spec);
        }
    }

    public void generateYaclScreenFactory(List<ConfigurableElement> roots, Function<ConfigurableElement, String> externalRefGetter) throws IOException {
        String spec = YACL_SCREEN_FACTORY_SPEC;
        YaclRoot root = createYaclImpl(roots, externalRefGetter);
        spec = spec.replaceAll("<yacl>", root.getSpec(4));

        StringBuilder importBuilder = new StringBuilder();
        for(String neededImport : root.getNeededImports()) {
            importBuilder.append("import ")
                    .append(neededImport)
                    .append(";\n");
        }
        spec = spec.replaceAll("<imports>", importBuilder.toString());

        spec = applyReplacements(spec);
        JavaFileObject configLoader = filer.createSourceFile("%s.client.YaclScreenFactory".formatted(settings.packageName()));
        try (PrintWriter out = new PrintWriter(configLoader.openWriter())) {
            out.println(spec);
        }
    }

    private YaclRoot createYaclImpl(List<ConfigurableElement> roots, Function<ConfigurableElement, String> externalRefGetter) {
        Map<String, List<ConfigurableElement>> elementCategories = new HashMap<>();
        for (ConfigurableElement element : roots) {
            String category = element.getCategory();
            elementCategories.computeIfAbsent(category, k -> new ArrayList<>()).add(element);
        }
        YaclCategories categories = new YaclCategories();
        String configName = settings.name();
        elementCategories.forEach((categoryName, entries) -> {
            YaclOptions options = new YaclOptions();
            YaclOptionGroups optionGroups = new YaclOptionGroups();
            entries.forEach(entry -> entry.disolveMultiLevelParents().forEach(parentOrChild -> {
                if(parentOrChild.childless()) {
                    options.addOption(createYaclOption(parentOrChild, configName, externalRefGetter));
                } else {
                    String key = parentOrChild.getKey();
                    YaclOptions entryOptions = new YaclOptions();
                    parentOrChild.children().forEach(child -> entryOptions.addOption(createYaclOption(child, configName, externalRefGetter)));
                    optionGroups.addOptionGroup(new YaclOptionGroup(
                            new YaclOptionGroupName(configName, key),
                            new YaclDescription(
                                    new YaclDescriptionText(configName, key),
                                    null
                            ),
                            entryOptions
                    ));
                }
            }));

            categories.addCategory(new YaclCategory(
                    new YaclCategoryName(configName, categoryName),
                    new YaclCategoryTooltip(configName, categoryName),
                    options,
                    optionGroups
            ));
        });

        return new YaclRoot(
                new YaclTitle(configName),
                categories,
                new YaclSave(configName)
        );
    }

    private @NotNull YaclOption createYaclOption(ConfigurableElement entry, String configName, Function<ConfigurableElement, String> externalRefGetter) {
        String key = entry.getKey();
        String type = entry.getBoxedType(types);
        String externalRef = externalRefGetter.apply(entry);
        return new YaclOption(
                type,
                new YaclOptionName(configName, key),
                new YaclDescription(
                        new YaclDescriptionText(configName, key),
                        null
                ),
                new YaclOptionBinding(externalRef),
                getOptionController(entry, externalRef)
        );
    }

    private @NotNull YaclOptionController getOptionController(ConfigurableElement entry, String externalRef) {
        return switch (entry.getControllerType()) {
            case AUTO -> switch (entry.getTypeKind()) {
                case BOOLEAN -> new YaclOptionController.TickBox();
                case BYTE, SHORT, INT -> new YaclOptionController.IntegerSlider(
                        new YaclValueFormatter(),
                        (int) entry.annotation().min(),
                        (int) entry.annotation().max()
                );
                case DOUBLE -> new YaclOptionController.DoubleSlider(
                        new YaclValueFormatter(),
                        entry.annotation().min(),
                        entry.annotation().max()
                );
                case CHAR -> new YaclOptionController.StringField();
                case FLOAT -> new YaclOptionController.FloatSlider(
                        new YaclValueFormatter(),
                        (float) entry.annotation().min(),
                        (float) entry.annotation().max()
                );
                case LONG -> new YaclOptionController.LongSlider(
                        new YaclValueFormatter(),
                        (long) entry.annotation().min(),
                        (long) entry.annotation().max()
                );
                case DECLARED -> {
                    ElementKind elementKind = entry.element().getKind();
                    if (elementKind == ElementKind.ENUM) {
                        yield new YaclOptionController.CyclingEnum(
                                new YaclValueFormatter(),
                                entry.getFullyQualifiedTypeName(types)
                        );
                    } else if (elementKind == ElementKind.CLASS) {
                        TypeElement string = elements.getTypeElement("java.lang.String");
                        if (types.isSameType(entry.getType(), string.asType())) {
                            yield new YaclOptionController.StringField();
                        }
                        TypeElement iterable = elements.getTypeElement("java.lang.Iterable");
                        if (types.isAssignable(entry.getType(), iterable.asType())) {
                            yield new YaclOptionController.CyclingList(
                                    new YaclValueFormatter(),
                                    externalRef
                            );
                        }
                    }
                    messager.printError("Could not automatically create controller for type: %s".formatted(entry.getFullyQualifiedTypeName(types)), entry.element());
                    throw new RuntimeException();
                }
                case ARRAY -> new YaclOptionController.CyclingList(
                        new YaclValueFormatter(),
                        externalRef
                );
                default -> {
                    messager.printError("Could not automatically create controller for type: %s".formatted(entry.getFullyQualifiedTypeName(types)), entry.element());
                    throw new RuntimeException();
                }
            };
            case BOOL -> new YaclOptionController.Bool(new YaclValueFormatter());
            case COLOR -> new YaclOptionController.Color(false);
            case COLOR_WITH_ALPHA -> new YaclOptionController.Color(true);
            case CYCLING_LIST -> new YaclOptionController.CyclingList(
                    new YaclValueFormatter(),
                    externalRef
            );
            case DOUBLE_FIELD -> new YaclOptionController.DoubleField(
                    new YaclValueFormatter(),
                    entry.annotation().min(),
                    entry.annotation().max()
            );
            case DOUBLE_SLIDER -> new YaclOptionController.DoubleSlider(
                    new YaclValueFormatter(),
                    entry.annotation().min(),
                    entry.annotation().max()
            );
            case ENUM -> new YaclOptionController.CyclingEnum(
                    new YaclValueFormatter(),
                    entry.getFullyQualifiedTypeName(types)
            );
            case ENUM_DROPDOWN -> new YaclOptionController.EnumDropdown(new YaclValueFormatter());
            case FLOAT_FIELD -> new YaclOptionController.FloatField(
                    new YaclValueFormatter(),
                    (float) entry.annotation().min(),
                    (float) entry.annotation().max()
            );
            case FLOAT_SLIDER -> new YaclOptionController.FloatSlider(
                    new YaclValueFormatter(),
                    (float) entry.annotation().min(),
                    (float) entry.annotation().max()
            );
            case INTEGER_FIELD -> new YaclOptionController.IntegerField(
                    new YaclValueFormatter(),
                    (int) entry.annotation().min(),
                    (int) entry.annotation().max()
            );
            case INTEGER_SLIDER -> new YaclOptionController.IntegerSlider(
                    new YaclValueFormatter(),
                    (int) entry.annotation().min(),
                    (int) entry.annotation().max()
            );
            case ITEM -> new YaclOptionController.Item();
            case LONG_FIELD -> new YaclOptionController.LongField(
                    new YaclValueFormatter(),
                    (long) entry.annotation().min(),
                    (long) entry.annotation().max()
            );
            case LONG_SLIDER -> new YaclOptionController.LongSlider(
                    new YaclValueFormatter(),
                    (long) entry.annotation().min(),
                    (long) entry.annotation().max()
            );
            case STRING_FIELD -> new YaclOptionController.StringField();
            case TICK_BOX -> new YaclOptionController.TickBox();
        };
    }
}
