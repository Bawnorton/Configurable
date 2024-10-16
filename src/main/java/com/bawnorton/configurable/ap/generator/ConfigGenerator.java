package com.bawnorton.configurable.ap.generator;

import com.bawnorton.configurable.ap.tree.ConfigurableElement;
import com.bawnorton.configurable.ap.tree.ConfigurableHolder;
import com.bawnorton.configurable.load.ConfigurableSettings;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
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
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.ref.constraint.*;
import com.bawnorton.configurable.ref.Reference;

public final class Config implements GeneratedConfig {
<content>
    @Override
    public void update(boolean fromServer) {
        boolean serverEnforces = ConfigurableMain.getWrappers("<name>").get("<source_set>").serverEnforces();
<validator>
    }
}
""";

    public Map<ConfigurableElement, String> externalReferenceMap = new HashMap<>();

    public ConfigGenerator(Filer filer, Types types, Messager messager, ConfigurableSettings settings) {
        super(filer, types, messager, settings);
    }

    public void generateConfig(List<ConfigurableElement> roots) throws IOException {
        StringBuilder validator = new StringBuilder();
        StringBuilder content = new StringBuilder();
        Set<String> neededImports = new HashSet<>();
        roots.forEach(root -> addElement(content, root, validator, neededImports, "config", 1));

        String spec = CONFIG_SPEC;
        spec = spec.replaceAll("<content>", content.toString());
        spec = spec.replaceAll("<validator>", validator.toString());

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

    private void addElement(StringBuilder builder, ConfigurableElement element, StringBuilder validator, Set<String> neededImports, String externalParent, int depth) {
        if (element.childless()) {
            addReference(builder, element, validator, neededImports, externalParent, depth);
        } else {
            addContainer(builder, element, validator, neededImports, externalParent, depth);
        }
    }

    private void addContainer(StringBuilder builder, ConfigurableElement element, StringBuilder validator, Set<String> neededImports, String externalParent, int depth) {
        String container = """
        %1$spublic final %2$s %3$s = new %2$s();
        
        %1$spublic static class %2$s {
        <comment>
        <content>
        %1$s}
        """.formatted(
                "\t".repeat(depth),
                element.getElementConfigName(),
                element.getKey()
        );
        StringBuilder contentBuilder = new StringBuilder();
        for (ConfigurableElement child : element.children()) {
            addElement(contentBuilder, child, validator, neededImports, "%s.%s".formatted(externalParent, element.getKey()), depth + 1);
        }
        container = container.replaceAll("<content>", contentBuilder.toString().replaceAll("\\r?\\n$", ""));
        container = container.replaceAll("<comment>", "%spublic static final String CONFIGURABLE_COMMENT = \"\"\"\n%s\"\"\".trim();".formatted("\t".repeat(depth), element.comment()));
        builder.append(container);
    }

    private void addReference(StringBuilder builder, ConfigurableElement element, StringBuilder validator, Set<String> neededImports, String externalParent, int depth) {
        String reference = "public final Reference<%s> %s = new Reference<>(\"%s\", %s.class, %s.class, \"\"\"\n%s\"\"\".trim(), %s);".formatted(
                element.getBoxedType(types),
                element.getKey(),
                element.getElementName(),
                element.getOwnerName(),
                element.getTypeName(),
                element.comment(),
                createConstraintSet(element)
        );
        neededImports.add(element.getFullyQualifiedTypeName(types));
        neededImports.add(element.getFullyQualifiedOwnerName(types));
        builder.append("\t".repeat(depth)).append(reference).append("\n");

        String externalReference = "%s.%s".formatted(externalParent, element.getKey());
        externalReferenceMap.put(element, externalReference);

        String internalReference = externalReference.substring(externalReference.indexOf(".") + 1);
        validator.append("\t".repeat(2));
        if(element.defaultServerEnforces()) {
            validator.append("%1$s.update(fromServer, serverEnforces);".formatted(internalReference));
        } else {
            validator.append("%1$s.update(fromServer, %2$s);".formatted(internalReference, element.annotationHolder().serverEnforces()));
        }
        validator.append("\n");
    }

    private String createConstraintSet(ConfigurableElement element) {
        ConfigurableHolder holder = element.annotationHolder();
        StringBuilder constraintSet = new StringBuilder("ConstraintSet.builder()");
        addPredicateConstraint(element, constraintSet);
        addRegexConstraint(holder, constraintSet);
        addClampedConstraint(holder, element.element(), constraintSet);
        return constraintSet.toString();
    }

    private void addPredicateConstraint(ConfigurableElement element, StringBuilder constraintSet) {
        String predicate = element.annotationHolder().predicate();
        if(predicate.isEmpty()) return;

        if(predicate.contains("#")) {
            String[] parts = predicate.split("#");
            String owner = parts[0];
            String methodName = parts[1];
            constraintSet.append(".addPredicate(value -> %s.%s((%s) value))".formatted(
                    owner,
                    methodName,
                    element.getTypeName()
            ));
        } else {
            String owner = element.getFullyQualifiedOwnerName(types);
            constraintSet.append(".addPredicate(value -> %s.%s((%s) value))".formatted(
                    owner,
                    predicate,
                    element.getTypeName()
            ));
        }
    }

    private void addRegexConstraint(ConfigurableHolder holder, StringBuilder constraintSet) {
        String regex = holder.regex();
        if(!regex.isEmpty()) {
            constraintSet.append(".addRegex(\"%s\")".formatted(regex));
        }
    }

    private void addClampedConstraint(ConfigurableHolder holder, Element element, StringBuilder constraintSet) {
        if (holder.isMaxSet() || holder.isMinSet()) {
            double min = holder.min();
            double max = holder.max();
            if (min > max) {
                messager.printMessage(Diagnostic.Kind.ERROR, "min must be smaller than or equal to the max", element);
            }
            constraintSet.append(".addClamped(%s, %s)".formatted(min, max));
        }
    }

    public String getExternalReference(ConfigurableElement element) {
        return externalReferenceMap.get(element);
    }
}