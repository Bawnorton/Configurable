package com.bawnorton.configurable.util;

public interface Either<A, B> {
	static <A, B> Either<A, B> left(A value) {
		return new Left<>(value);
	}

	static <A, B> Either<A, B> right(B value) {
		return new Right<>(value);
	}

	boolean isLeft();

	boolean isRight();

	A getLeft();

	B getRight();

	class Left<A, B> implements Either<A, B> {
		private final A value;

		public Left(A value) {
			this.value = value;
		}

		@Override
		public boolean isLeft() {
			return true;
		}

		@Override
		public boolean isRight() {
			return false;
		}

		@Override
		public A getLeft() {
			return value;
		}

		@Override
		public B getRight() {
			throw new UnsupportedOperationException("No right value present");
		}
	}

	class Right<A, B> implements Either<A, B> {
		private final B value;

		public Right(B value) {
			this.value = value;
		}

		@Override
		public boolean isLeft() {
			return false;
		}

		@Override
		public boolean isRight() {
			return true;
		}

		@Override
		public A getLeft() {
			throw new UnsupportedOperationException("No left value present");
		}

		@Override
		public B getRight() {
			return value;
		}
	}
}
