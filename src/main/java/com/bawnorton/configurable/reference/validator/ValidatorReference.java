package com.bawnorton.configurable.reference.validator;

import java.util.function.Supplier;

public record ValidatorReference<T>(boolean fallback, Supplier<T> defaultSupplier, FieldValidator<T> fieldValidator,
                                    MessageProvider<T> messageProvider) {
	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	public static class Builder<T> {
		private boolean fallback = false;
		private Supplier<T> defaultSupplier = null;
		private FieldValidator<T> fieldValidator = null;
		private MessageProvider<T> messageProvider = null;

		public Builder<T> fallback(boolean fallback) {
			this.fallback = fallback;
			return this;
		}

		public Builder<T> defaultSupplier(Supplier<T> defaultSupplier) {
			this.defaultSupplier = defaultSupplier;
			return this;
		}

		public Builder<T> fieldValidator(FieldValidator<T> fieldValidator) {
			this.fieldValidator = fieldValidator;
			return this;
		}

		public Builder<T> messageProvider(MessageProvider<T> messageProvider) {
			this.messageProvider = messageProvider;
			return this;
		}

		public ValidatorReference<T> build() {
			return new ValidatorReference<>(fallback, defaultSupplier, fieldValidator, messageProvider);
		}
	}
}
