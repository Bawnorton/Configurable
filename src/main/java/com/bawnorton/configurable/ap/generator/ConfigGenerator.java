package com.bawnorton.configurable.ap.generator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.ap.tree.ConfigurableElement;
import com.bawnorton.configurable.impl.ConfigurableSettings;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfigGenerator extends ConfigurableGenerator {

    //language=Java
    private static final String CONFIG_SPEC = """

package <configurable_package>;

<imports>
import com.bawnorton.configurable.impl.generated.GeneratedConfig;
import com.bawnorton.configurable.impl.ref.Reference;
import com.bawnorton.configurable.impl.ref.constraint.*;

public final class Config implements GeneratedConfig {
<content>
}
    """;

    public Map<ConfigurableElement, String> externalReferenceMap = new HashMap<>();

    public ConfigGenerator(Filer filer, Types types, Messager messager, ConfigurableSettings settings) {
        super(filer, types, messager, settings);
    }

    public void generateConfig(List<ConfigurableElement> roots) throws IOException {
        StringBuilder content = new StringBuilder();
        Set<String> neededImports = new HashSet<>();
        roots.forEach(root -> addElement(content, root, neededImports, "config", 1));
        
        String spec = CONFIG_SPEC;
        spec = spec.replaceAll("<content>", content.toString());

        StringBuilder importBuilder = new StringBuilder();
        for(String neededImport : neededImports) {
            importBuilder.append("import ")
                    .append(neededImport)
                    .append(";\n");
        }
        spec = spec.replaceAll("<imports>", importBuilder.toString());

        spec = applyReplacements(spec);
        JavaFileObject config = filer.createSourceFile(settings.fullyQualifiedConfig());
        try (PrintWriter out = new PrintWriter(config.openWriter())) {
            out.println(spec);
        }
    }

    private void addElement(StringBuilder builder, ConfigurableElement element, Set<String> neededImports, String externalParent, int depth) {
        if (element.childless()) {
            addReference(builder, element, neededImports, externalParent, depth);
        } else {
            String container = """
            %1$spublic final %2$s %3$s = new %2$s();
            
            %1$spublic static class %2$s {
            <content>
            %1$s}
            """.formatted(
                    "\t".repeat(depth),
                    element.getElementConfigName(),
                    element.getKey()
            );
            StringBuilder contentBuilder = new StringBuilder();
            for (ConfigurableElement child : element.children()) {
                addElement(contentBuilder, child, neededImports, "%s.%s".formatted(externalParent, element.getKey()), depth + 1);
            }
            container = container.replaceAll("<content>", contentBuilder.toString().replaceAll("\\r?\\n$", ""));
            builder.append(container);
        }
    }

    private void addReference(StringBuilder builder, ConfigurableElement element, Set<String> neededImports, String externalParent, int depth) {
        String reference = "public final Reference<%s> %s = new Reference<>(\"%s\", %s.class, %s.class, %s);".formatted(
                element.getBoxedType(types),
                element.getKey(),
                element.getElementName(),
                element.getOwnerName(),
                element.getTypeName(),
                createConstraintSet(element)
        );
        neededImports.add(element.getFullyQualifiedTypeName(types));
        neededImports.add(element.getFullyQualifiedOwnerName(types));
        builder.append("\t".repeat(depth)).append(reference).append("\n");

        String externalReference = "%s.%s".formatted(externalParent, element.getKey());
        externalReferenceMap.put(element, externalReference);
    }

    private String createConstraintSet(ConfigurableElement element) {
        Configurable annotation = element.annotation();
        StringBuilder constraintSet = new StringBuilder("ConstraintSet.builder()");
        addPredicateConstraint(annotation, constraintSet);
        addRegexConstraint(annotation, constraintSet);
        addClampedConstraint(annotation, element.element(), constraintSet);
        return constraintSet.toString();
    }

    private void addPredicateConstraint(Configurable annotation, StringBuilder constraintSet) {
        String predicate = annotation.predicate();
        if(!predicate.isBlank()) {
            constraintSet.append(".addPredicate(\"%s\")".formatted(predicate));
        }
    }

    private void addRegexConstraint(Configurable annotation, StringBuilder constraintSet) {
        String regex = annotation.regex();
        if(!regex.isBlank()) {
            constraintSet.append(".addRegex(\"%s\")".formatted(regex));
        }
    }

    private void addClampedConstraint(Configurable annotation, Element element, StringBuilder constraintSet) {
        double min = annotation.min();
        double max = annotation.max();
        if (max != Double.MAX_VALUE || min != Double.MIN_NORMAL) {
            if (min > max) {
                messager.printError("min must be smaller than or equal to the max", element);
            }
            constraintSet.append(".addClamped(%s, %s)".formatted(min, max));
        }
    }

    public String getExternalReference(ConfigurableElement element) {
        return externalReferenceMap.get(element);
    }
}