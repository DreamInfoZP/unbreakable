package com.uaepay.rm.unbreakable;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.uaepay.rm.unsafe.Cast;

public abstract class Option<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Option() {
    }

    public static <T> Option<T> some(T value) {
        Objects.requireNonNull(value, "Option.some value should not be null");
        Some<T> inst = new Some<>();
        inst.value = value;
        return inst;
    }

    public static <T> Option<T> none() {
        return Cast.cast(None.instance);
    }

    private static <T> Option<T> of(T value) {
        if (value == null)
            return none();
        return some(value);
    }

    public static <T> Option<T> lift(T value) {
        return of(value);
    }

    public abstract T unsafeGet();

    public abstract T getOrElse(Supplier<T> orElse);

    public abstract boolean isSome();

    public abstract void foreach(Consumer<T> func);

    public boolean isNone() {
        return !isSome();
    }

    public abstract <R> Option<R> map(Function<T, R> map);

    public static <A, B> Option<B> map(Option<A> optA, Function<A, B> func) {
        return optA.map(func);
    }

    public static <A, B, C> Option<C> map2(Option<A> optA, Option<B> optB, Function2<A, B, C> func) {
        return optA.flatMap(a -> {
            return optB.map(b -> {
                return func.apply(a, b);
            });
        });
    }

    public <A, B> Option<B> map2(Option<A> optA, Function2<T, A, B> func) {
        return Option.map2(this, optA, func);
    }

    public static <A, B, C, D> Option<D> map3(Option<A> optA, Option<B> optB, Option<C> optC,
            Function3<A, B, C, D> func) {
        Option<Option<D>> ret = Option.map2(optA, optB, (a, b) -> {
            return optC.map(c -> func.apply(a, b, c));
        });
        return flatten(ret);
    }

    public <A, B, C> Option<C> map3(Option<A> optA, Option<B> optB, Function3<T, A, B, C> func) {
        return Option.map3(this, optA, optB, func);
    }

    public static <A, B, C, D, E> Option<E> map4(Option<A> optA, Option<B> optB, Option<C> optC, Option<D> optD,
            Function4<A, B, C, D, E> func) {
        Option<Option<E>> ret = Option.map3(optA, optB, optC, (a, b, c) -> {
            return optD.map(d -> func.apply(a, b, c, d));
        });
        return flatten(ret);
    }

    public <A, B, C, D> Option<D> map4(Option<A> optA, Option<B> optB, Option<C> optC, Function4<T, A, B, C, D> func) {
        return Option.map4(this, optA, optB, optC, func);
    }

    public static <A, B, C, D, E, F> Option<F> map5(Option<A> optA, Option<B> optB, Option<C> optC, Option<D> optD,
            Option<E> optE, Function5<A, B, C, D, E, F> func) {
        return flatten(Option.map4(optA, optB, optC, optD, (a, b, c, d) -> optE.map(e -> func.apply(a, b, c, d, e))));
    }

    public <A, B, C, D, E> Option<E> map5(Option<A> optA, Option<B> optB, Option<C> optC, Option<D> optD,
            Function5<T, A, B, C, D, E> func) {
        return Option.map5(this, optA, optB, optC, optD, func);
    }

    public <R> Option<R> flatMap(Function<T, Option<R>> func) {
        return flatten(map(func));
    }

    public static <T> Option<T> flatten(Option<Option<T>> opt) {
        if (opt == null)
            return none();
        return opt.getOrElse(() -> none());
    }

    public final static class Some<T> extends Option<T> {
        private static final long serialVersionUID = 1L;
        private T value;

        private Some() {
        }

        @Override
        public T unsafeGet() {
            return value;
        }

        @Override
        public String toString() {
            return "Some(" + value.toString() + ")";
        }

        @Override
        public T getOrElse(Supplier<T> orElse) {
            return value;
        }

        @Override
        public <R> Option<R> map(Function<T, R> map) {
            Function<T, R> unsafe = Objects.requireNonNull(map, "map function should not be null");
            R result = unsafe.apply(value);
            Option<R> x = lift(result);
            return x;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Some<?> s = (Some<?>) obj;
            return s.value.equals(value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean isSome() {
            return true;
        }

        @Override
        public void foreach(Consumer<T> func) {
            if (func != null) {
                func.accept(this.value);
            }
        }
    }

    public final static class None<T> extends Option<T> {

        private static final long serialVersionUID = 1L;

        private static None<?> instance = new None<Object>();

        private None() {
        }

        @Override
        public T unsafeGet() {
            throw new RuntimeException("Can't get anything from None");
        }

        @Override
        public String toString() {
            return "None";
        }

        @Override
        public T getOrElse(Supplier<T> orElse) {
            Supplier<T> unsafe = Objects.requireNonNull(orElse, "Option.getOrElse#orElse should not be null");
            return unsafe.get();
        }

        @Override
        public <R> Option<R> map(Function<T, R> map) {
            return none();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            return "None".hashCode();
        }

        @Override
        public boolean isSome() {
            return false;
        }

        @Override
        public void foreach(Consumer<T> func) {
        }
    }
}