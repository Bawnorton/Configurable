package com.bawnorton.configurable.util;

public record Pair<A, B>(A a, B b) {
    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    public A first() {
        return a;
    }

    public B second() {
        return b;
    }
}
