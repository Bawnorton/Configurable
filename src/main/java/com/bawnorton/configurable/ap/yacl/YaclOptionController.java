package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.ap.helper.MappingsHelper;
import java.util.function.Consumer;

public abstract class YaclOptionController extends YaclElement {
    protected final YaclElement valueFormatter;

    protected YaclOptionController(YaclElement valueFormatter) {
        this.valueFormatter = valueFormatter;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        if(valueFormatter != null) {
            valueFormatter.addNeededImports(adder);
        }
    }

    @Override
    protected String getSpec(int depth) {
        String controllerSpec = getControllerSpec(depth + 1);
        if (valueFormatter == null) {
            return "option -> %s".formatted(controllerSpec).trim();
        }

        return """
        option -> %2$s
        %1$s.formatValue(value -> %3$s)
        """.formatted(
                "\t".repeat(depth + 1),
                controllerSpec,
                valueFormatter.getSpec(depth + 1)
        ).trim();
    }

    protected abstract String getControllerSpec(int depth);

    public static class Bool extends YaclOptionController {
        public Bool(YaclElement valueFormatter) {
            super(valueFormatter);
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.BooleanControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return "BooleanControllerBuilder.create(option)";
        }
    }

    public static class Color extends YaclOptionController {
        private final boolean allowAlpha;

        public Color(boolean allowAlpha) {
            super(null);
            this.allowAlpha = allowAlpha;
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.ColorControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return """
            ColorControllerBuilder.create(option)
            %1$s.allowAlpha(%2$s)
            """.formatted(
                    "\t".repeat(depth),
                    allowAlpha
            ).trim();
        }
    }

    public static class CyclingList extends YaclOptionController {
        private final String values;

        public CyclingList(YaclElement valueFormatter, String values) {
            super(valueFormatter);
            this.values = values;
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.CyclingListControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return """
            CyclingListControllerBuilder.create(option)
            %1$s.values(%2$s)
            """.formatted(
                    "\t".repeat(depth),
                    values
            ).trim();
        }
    }

    public static class DoubleField extends NumberField<Double> {
        public DoubleField(YaclElement valueFormatter, double min, double max) {
            super(valueFormatter, "Double", "", min, max);
        }
    }

    public static class DoubleSlider extends NumberSlider<Double> {
        public DoubleSlider(YaclElement valueFormatter, double min, double max) {
            super(valueFormatter, "Double", "", min, max);
        }

        @Override
        protected String getStep(Double max) {
            return "%s / 100".formatted(max);
        }
    }

    public static class CyclingEnum extends YaclOptionController {
        private final String enumClass;

        public CyclingEnum(YaclElement valueFormatter, String enumClass) {
            super(valueFormatter);
            this.enumClass = enumClass;
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept(enumClass);
            adder.accept("dev.isxander.yacl3.api.controller.EnumControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return """
            EnumControllerBuilder.create(option)
            %1$s.enumClass(%2$s.class)
            """.formatted(
                    "\t".repeat(depth),
                    enumClass.substring(enumClass.lastIndexOf(".") + 1)
            ).trim();
        }
    }

    public static class EnumDropdown extends YaclOptionController {
        public EnumDropdown(YaclElement valueFormatter) {
            super(valueFormatter);
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return "EnumDropdownControllerBuilder.create(option)";
        }
    }

    public static class FloatField extends NumberField<Float> {
        public FloatField(YaclElement valueFormatter, float min, float max) {
            super(valueFormatter, "Float", "F", min, max);
        }
    }

    public static class FloatSlider extends NumberSlider<Float> {
        public FloatSlider(YaclElement valueFormatter, float min, float max) {
            super(valueFormatter, "Float", "F", min, max);
        }

        @Override
        protected String getStep(Float max) {
            return "%sF / 100".formatted(max);
        }
    }

    public static class IntegerField extends NumberField<Integer> {
        public IntegerField(YaclElement valueFormatter, int min, int max) {
            super(valueFormatter, "Integer", "", min, max);
        }
    }

    public static class IntegerSlider extends NumberSlider<Integer> {
        public IntegerSlider(YaclElement valueFormatter, int min, int max) {
            super(valueFormatter, "Integer", "", min, max);
        }

        @Override
        protected String getStep(Integer max) {
            return "Math.max(1, %s / 100)".formatted(max);
        }
    }

    public static class Item extends YaclOptionController {
        public Item() {
            super(null);
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.ItemControllerBuilder");
            adder.accept(MappingsHelper.getItem());
        }

        @Override
        protected String getControllerSpec(int depth) {
            return "ItemControllerBuilder.create(option)";
        }
    }

    public static class LongField extends NumberField<Long> {
        public LongField(YaclElement valueFormatter, long min, long max) {
            super(valueFormatter, "Long", "L", min, max);
        }
    }

    public static class LongSlider extends NumberSlider<Long> {
        public LongSlider(YaclElement valueFormatter, long min, long max) {
            super(valueFormatter, "Long", "L", min, max);
        }

        @Override
        protected String getStep(Long max) {
            return "Math.max(1, %sL / 100)".formatted(max);
        }
    }

    public static class StringField extends YaclOptionController {
        public StringField() {
            super(null);
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.StringControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return "StringControllerBuilder.create(option)";
        }
    }

    public static class TickBox extends YaclOptionController {
        public TickBox() {
            super(null);
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.TickBoxControllerBuilder");
        }

        @Override
        protected String getControllerSpec(int depth) {
            return "TickBoxControllerBuilder.create(option)";
        }
    }

    private static abstract class NumberField<T extends Number> extends YaclOptionController {
        private final String name;
        private final String suffix;
        private final T min;
        private final T max;

        protected NumberField(YaclElement valueFormatter, String name, String suffix, T min, T max) {
            super(valueFormatter);
            this.name = name;
            this.suffix = suffix;
            this.min = min;
            this.max = max;
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.%sFieldControllerBuilder".formatted(name));
        }

        @Override
        protected String getControllerSpec(int depth) {
            return """
            %2$sFieldControllerBuilder.create(option)
            %1$s.range(%3$s, %4$s)
            """.formatted(
                    "\t".repeat(depth),
                    name,
                    min + suffix,
                    max + suffix
            ).trim();
        }
    }

    private static abstract class NumberSlider<T extends Number> extends YaclOptionController {
        private final String name;
        private final String suffix;
        private final T min;
        private final T max;

        protected NumberSlider(YaclElement valueFormatter, String name, String suffix, T min, T max) {
            super(valueFormatter);
            this.name = name;
            this.suffix = suffix;
            this.min = min;
            this.max = max;
        }

        @Override
        protected void addNeededImports(Consumer<String> adder) {
            super.addNeededImports(adder);
            adder.accept("dev.isxander.yacl3.api.controller.%sSliderControllerBuilder".formatted(name));
        }

        protected abstract String getStep(T max);

        @Override
        protected String getControllerSpec(int depth) {
            return """
            %2$sSliderControllerBuilder.create(option)
            %1$s.range(%3$s, %4$s)
            %1$s.step(%5$s)
            """.formatted(
                    "\t".repeat(depth),
                    name,
                    min + suffix,
                    max + suffix,
                    getStep(max)
            ).trim();
        }
    }
}
