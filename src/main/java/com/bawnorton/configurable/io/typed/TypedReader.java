package com.bawnorton.configurable.io.typed;

import com.bawnorton.configurable.util.GenericType;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;

public class TypedReader<T> {
	private JsonReader<T> jsonReader;
	private TomlReader<T> tomlReader;
	private ObjectReader<T> objectReader;
	private ByteBufReader<T> byteBufReader;

	public static <T> TypedReader<T> create() {
		return new TypedReader<>();
	}

	public TypedReader<T> json(JsonReader<T> reader) {
		this.jsonReader = reader;
		return this;
	}

	public TypedReader<T> toml(TomlReader<T> reader) {
		this.tomlReader = reader;
		return this;
	}

	public TypedReader<T> object(ObjectReader<T> reader) {
		this.objectReader = reader;
		return this;
	}

	public TypedReader<T> byteBuf(ByteBufReader<T> reader) {
		this.byteBufReader = reader;
		return this;
	}

	T readJson(JsonElement element, GenericType expectedType) {
		if (jsonReader == null) throw new UnsupportedOperationException("JSON reading not supported for this type");
		return jsonReader.read(element, expectedType);
	}

	T readToml(CommentedConfig config, String path, GenericType expectedType) {
		if (tomlReader == null) throw new UnsupportedOperationException("TOML reading not supported for this type");
		return tomlReader.read(config, path, expectedType);
	}

	T readObject(Object item, GenericType expectedType) {
		if (objectReader == null) throw new UnsupportedOperationException("Object reading not supported for this type");
		try {
			return objectReader.read(item, expectedType);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Failed to read object: " + item + " of type " + item.getClass()
					.getName(), e);
		}
	}

	T readByteBuf(ByteBuf buf, GenericType expectedType) {
		if (byteBufReader == null)
			throw new UnsupportedOperationException("ByteBuf reading not supported for this type");
		return byteBufReader.read(buf, expectedType);
	}

	public interface JsonReader<T> {
		static <T> Contextless<T> contextless(JsonReader.Contextless<T> reader) {
			return reader;
		}

		T read(JsonElement element, GenericType expectedType);

		interface Contextless<T> extends JsonReader<T> {
			T read(JsonElement element);

			@Override
			default T read(JsonElement element, GenericType expectedType) {
				return read(element);
			}
		}
	}

	public interface TomlReader<T> {
		static <T> Contextless<T> contextless(TomlReader.Contextless<T> reader) {
			return reader;
		}

		T read(CommentedConfig config, String path, GenericType expectedType);

		interface Contextless<T> extends TomlReader<T> {
			T read(CommentedConfig config, String path);

			@Override
			default T read(CommentedConfig config, String path, GenericType expectedType) {
				return read(config, path);
			}
		}
	}

	public interface ObjectReader<T> {
		static <T> Contextless<T> contextless(ObjectReader.Contextless<T> reader) {
			return reader;
		}

		T read(Object item, GenericType expectedType);

		interface Contextless<T> extends ObjectReader<T> {
			T read(Object item);

			@Override
			default T read(Object item, GenericType expectedType) {
				return read(item);
			}
		}
	}

	public interface ByteBufReader<T> {
		static <T> Contextless<T> contextless(ByteBufReader.Contextless<T> reader) {
			return reader;
		}

		T read(ByteBuf buf, GenericType expectedType);

		interface Contextless<T> extends ByteBufReader<T> {
			T read(ByteBuf buf);

			@Override
			default T read(ByteBuf buf, GenericType expectedType) {
				return read(buf);
			}
		}
	}
}
