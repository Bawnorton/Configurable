package com.bawnorton.configurable.networking;

import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.api.serialisation.SerialisationApi;
import com.bawnorton.configurable.reference.FieldReference;
import com.bawnorton.configurable.service.ConfigLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SyncConfigPayload(String name, List<FieldReference<?>> fieldReferences) implements CustomPacketPayload {
	public static final Identifier ID = ConfigurableMain.rl("sync_config_payload");
	public static final CustomPacketPayload.Type<SyncConfigPayload> TYPE = new Type<>(ID);
	public static final StreamCodec<ByteBuf, SyncConfigPayload> STREAM_CODEC = StreamCodec.of(
			SyncConfigPayload::encode,
			SyncConfigPayload::decode
	);

	private static void encode(ByteBuf byteBuf, SyncConfigPayload payload) {
		ByteBufCodecs.STRING_UTF8.encode(byteBuf, payload.name());
		List<FieldReference<?>> fieldReferences = payload.fieldReferences()
				.stream()
				.filter(FieldReference::doesSync)
				.toList();
		ByteBufCodecs.VAR_INT.encode(byteBuf, fieldReferences.size());
		for (FieldReference<?> fieldReference : fieldReferences) {
			ByteBufCodecs.STRING_UTF8.encode(byteBuf, fieldReference.fullName());
			Object value = fieldReference.get();
			SerialisationApi.encodeByteBuf(byteBuf, value, fieldReference.genericType());
		}
	}

	private static SyncConfigPayload decode(ByteBuf byteBuf) {
		String name = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
		if (!ConfigurableLoader.isConfigLoaderPresent(name)) {
			return new SyncConfigPayload(name, List.of());
		}
		ConfigLoader configLoader = ConfigurableLoader.getConfigLoader(name);
		List<FieldReference<?>> expectedFields = configLoader.getFields()
				.stream()
				.filter(FieldReference::doesSync)
				.toList();
		int size = ByteBufCodecs.VAR_INT.decode(byteBuf);
		if (size != expectedFields.size()) {
			throw new IllegalArgumentException("Expected %d fields, but got %d. There is likely a server-client version mismatch for '%s'".formatted(expectedFields.size(), size, name));
		}
		//noinspection unchecked
		Map<String, FieldReference<Object>> fieldMap = expectedFields.stream()
				.collect(Collectors.toMap(
						FieldReference::fullName,
						ref -> (FieldReference<Object>) ref
				));
		List<FieldReference<?>> newFieldReferences = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			String fullName = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
			FieldReference<Object> fieldReference = fieldMap.get(fullName);
			if (fieldReference == null) {
				throw new IllegalArgumentException("Field '%s' not found in config '%s'".formatted(fullName, name));
			}
			Object value = SerialisationApi.decode(byteBuf, fieldReference.genericType());
			newFieldReferences.add(
					FieldReference.builder(
									ignored -> {
										throw new UnsupportedOperationException("Synthetic networking field reference '%s' for '%s' is read-only".formatted(fullName, name));
									},
									() -> value,
									fieldReference.genericType(),
									fieldReference.name()
							)
							.comment(fieldReference.comment())
							.group(fieldReference.group())
							.doesSync(fieldReference.doesSync())
							.validator(fieldReference.validator())
							.build()
			);
		}
		return new SyncConfigPayload(name, newFieldReferences);
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void applyToConfigLoader(ConfigLoader configLoader) {
		List<FieldReference<?>> fields = configLoader.getFields();
		//noinspection unchecked
		Map<String, FieldReference<Object>> fieldMap = fields.stream()
				.filter(FieldReference::doesSync)
				.collect(Collectors.toMap(
						FieldReference::fullName,
						ref -> (FieldReference<Object>) ref
				));
		for (FieldReference<?> fieldReference : fieldReferences) {
			String fieldName = fieldReference.fullName();
			FieldReference<Object> matchingField = fieldMap.get(fieldName);
			if (matchingField != null) {
				matchingField.set(fieldReference.get(), true);
			}
		}
	}
}
