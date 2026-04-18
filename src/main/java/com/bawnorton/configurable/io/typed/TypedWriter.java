package com.bawnorton.configurable.io.typed;

import com.bawnorton.configurable.util.GenericType;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;

public class TypedWriter<T> {
	private JsonWriter<T> jsonWriter;
	private TomlWriter<T> tomlWriter;
	private ByteBufWriter<T> byteBufWriter;

	public static <T> TypedWriter<T> create() {
		return new TypedWriter<>();
	}

	public TypedWriter<T> byteBuf(ByteBufWriter<T> writer) {
		this.byteBufWriter = writer;
		return this;
	}

	public TypedWriter<T> json(JsonWriter<T> writer) {
		this.jsonWriter = writer;
		return this;
	}

	public TypedWriter<T> toml(TomlWriter<T> writer) {
		this.tomlWriter = writer;
		return this;
	}

	JsonElement writeJson(T item, GenericType expectedType) {
		if (jsonWriter == null) throw new UnsupportedOperationException("JSON writing not supported for this type");
		return jsonWriter.write(item, expectedType);
	}

	Object writeToml(T item, GenericType expectedType) {
		if (tomlWriter == null) throw new UnsupportedOperationException("TOML writing not supported for this type");
		return tomlWriter.write(item, expectedType);
	}

	void writeByteBuf(ByteBuf buf, T item, GenericType expectedType) {
		if (byteBufWriter == null)
			throw new UnsupportedOperationException("ByteBuf writing not supported for this type");
		byteBufWriter.write(buf, item, expectedType);
	}

	public interface ByteBufWriter<T> {
		static <T> Contextless<T> contextless(ByteBufWriter.Contextless<T> writer) {
			return writer;
		}

		void write(ByteBuf buf, T item, GenericType expectedType);

		interface Contextless<T> extends ByteBufWriter<T> {
			void write(ByteBuf buf, T item);

			@Override
			default void write(ByteBuf buf, T item, GenericType expectedType) {
				write(buf, item);
			}
		}
	}

	public interface JsonWriter<T> {
		static <T> Contextless<T> contextless(JsonWriter.Contextless<T> writer) {
			return writer;
		}

		JsonElement write(T item, GenericType expectedType);

		interface Contextless<T> extends JsonWriter<T> {
			JsonElement write(T item);

			@Override
			default JsonElement write(T item, GenericType expectedType) {
				return write(item);
			}
		}
	}

	public interface TomlWriter<T> {
		static <T> Contextless<T> contextless(TomlWriter.Contextless<T> writer) {
			return writer;
		}

		Object write(T item, GenericType expectedType);

		interface Contextless<T> extends TomlWriter<T> {
			Object write(T item);

			@Override
			default Object write(T item, GenericType expectedType) {
				return write(item);
			}
		}
	}
}
