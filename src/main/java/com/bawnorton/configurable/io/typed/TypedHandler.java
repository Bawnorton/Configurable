package com.bawnorton.configurable.io.typed;

import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

public final class TypedHandler<T> {
	private final TypedReader<T> reader;
	private final TypedWriter<T> writer;

	private GenericType expectedType;

	public TypedHandler(TypedReader<T> reader, TypedWriter<T> writer) {
		this.reader = reader;
		this.writer = writer;
	}

	public void attachExpectedType(GenericType expectedType) {
		this.expectedType = expectedType;
	}

	public T readFromJson(JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return null;
		}

		return reader.readJson(element, expectedType);
	}

	public T readFromToml(CommentedConfig config, String path) {
		if (config == null || !config.contains(path)) {
			return null;
		}

		return reader.readToml(config, path, expectedType);
	}

	public T readFromObject(Object item) {
		if (item == null) return null;

		return reader.readObject(item, expectedType);
	}

	public T readFromByteBuf(ByteBuf buf) {
		int nullCheck = ByteBufCodecs.VAR_INT.decode(buf);
		if (nullCheck == -1) {
			return null;
		} else if (nullCheck != 0) {
			throw new IllegalArgumentException("Expected a null check value of -1 or 0, but got: %d".formatted(nullCheck));
		}

		return reader.readByteBuf(buf, expectedType);
	}

	@SuppressWarnings("unchecked")
	public void writeToByteBuf(ByteBuf buf, Object item) {
		if (item == null) {
			ByteBufCodecs.VAR_INT.encode(buf, -1);
			return;
		} else {
			ByteBufCodecs.VAR_INT.encode(buf, 0);
		}

		writer.writeByteBuf(buf, (T) item, expectedType);
	}
}