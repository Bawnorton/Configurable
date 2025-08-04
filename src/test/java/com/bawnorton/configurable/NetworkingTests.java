package com.bawnorton.configurable;

import com.bawnorton.configurable.helper.ConfigurableTestHelper;
import com.bawnorton.configurable.networking.SyncConfigPayload;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.reference.validator.ValidatorReference;
import com.bawnorton.configurable.service.ConfigLoader;
import com.bawnorton.configurable.util.GenericType;
import com.google.testing.compile.Compilation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.StreamCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NetworkingTests extends BaseTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testPacketEncodesAndDecodesCorrectly() {
        ConfigurableTestHelper.logModule();
        Compilation compilation = testCompilationSuccess("sources/serialisation/BasicSerialisation.java");
        ConfigLoader instance = getLoaderFromCompilation(compilation);
        Path root = Path.of(System.getProperty("user.dir")).getParent().getParent();
        Path configDir = root.resolve("test-output/%s/".formatted(ConfigurableTestHelper.getModuleName()));
        ConfigurableMain.registerConfigLoader(configDir, instance);

        List<FieldReference<?>> fields = instance.getFields();
        List<FieldReference<?>> preSyncFields = new ArrayList<>();
        for (FieldReference<?> field : fields) {
            if (!field.doesSync()) continue;

            Holder holder = new Holder();
            holder.value = field.get();
            FieldReference<?> preSyncField = FieldReference.builder(
                value -> holder.value = value,
                () -> holder.value,
                field.genericType(),
                field.name())
                    .doesSync(true)
                    .comment(field.comment())
                    .group(field.group())
                    .validator((ValidatorReference<Object>) field.validator())
                    .build();
            preSyncFields.add(preSyncField);
        }

        SyncConfigPayload payload = new SyncConfigPayload(instance.getName(), preSyncFields);
        StreamCodec<ByteBuf, SyncConfigPayload> streamCodec = SyncConfigPayload.STREAM_CODEC;
        ByteBuf byteBuf = Unpooled.buffer();
        streamCodec.encode(byteBuf, payload);
        SyncConfigPayload decoded = streamCodec.decode(byteBuf);
        Assertions.assertEquals(payload.name(), decoded.name());

        decoded.applyToConfigLoader(instance);
        List<FieldReference<?>> appliedFields = instance.getFields()
                .stream()
                .filter(FieldReference::doesSync)
                .toList();

        Assertions.assertEquals(preSyncFields.size(), appliedFields.size());
        for (int i = 0; i < preSyncFields.size(); i++) {
            FieldReference<?> originalField = preSyncFields.get(i);
            FieldReference<?> decodedField = appliedFields.get(i);
            Assertions.assertEquals(originalField.fullName(), decodedField.fullName());
            Assertions.assertEquals(originalField.genericType(), decodedField.genericType());
            Assertions.assertEquals(originalField.comment(), decodedField.comment());
            Assertions.assertEquals(originalField.group(), decodedField.group());
            Assertions.assertEquals(originalField.validator(), decodedField.validator());

            Object originalValue = originalField.get();
            Object decodedValue = decodedField.get();
            assertValuesEqual(originalValue, decodedValue, originalField.genericType());
        }
    }

    private static void assertValuesEqual(Object originalValue, Object decodedValue, GenericType genericHolder) {
        Class<?> type = genericHolder.type();
        if (genericHolder.isRaw()) {
            if (type.isArray()) {
                int originalLength = Array.getLength(originalValue);
                int decodedLength = Array.getLength(decodedValue);
                Class<?> componentType = type.getComponentType();
                Assertions.assertEquals(originalLength, decodedLength);
                for (int j = 0; j < originalLength; j++) {
                    Object originalElement = Array.get(originalValue, j);
                    Object decodedElement = Array.get(decodedValue, j);
                    assertValuesEqual(originalElement, decodedElement, new GenericType(componentType));
                }
            } else {
                Assertions.assertEquals(originalValue, decodedValue);
            }
        } else {
            GenericType[] genericTypes = genericHolder.genericTypes();
            if (genericTypes.length == 1) {
                GenericType genericType = genericTypes[0];
                if (List.class.isAssignableFrom(type)) {
                    List<?> originalList = (List<?>) originalValue;
                    List<?> decodedList = (List<?>) decodedValue;
                    Assertions.assertEquals(originalList.size(), decodedList.size());
                    for (int j = 0; j < originalList.size(); j++) {
                        Object originalElement = originalList.get(j);
                        Object decodedElement = decodedList.get(j);
                        assertValuesEqual(originalElement, decodedElement, genericType);
                    }
                } else {
                    Assertions.fail("Unsupported generic type: %s".formatted(genericHolder));
                }
            } else {
                Assertions.fail("Generic type with multiple parameters is not supported: %s".formatted(genericHolder));
            }
        }
    }

    private static class Holder {
        Object value;
    }
}
