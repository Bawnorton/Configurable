package com.bawnorton.configurable.io.typed;

import com.bawnorton.configurable.util.GenericType;
import io.netty.buffer.ByteBuf;

public class TypedWriter<T> {
	private ByteBufWriter<T> byteBufWriter;

	public static <T> TypedWriter<T> create() {
		return new TypedWriter<>();
	}

	public TypedWriter<T> byteBuf(ByteBufWriter<T> writer) {
		this.byteBufWriter = writer;
		return this;
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
}
