package com.uaepay.rm.unbreakable;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com.uaepay.rm.unsafe.Cast;

public abstract class Result<L, R> implements Serializable {
  private static final long serialVersionUID = 1L;

  private Result() {
  }

  public static <L, R> Result<L, R> liftLeft(L value) {
    return new Left<L, R>(value);
  }

  public static <L, R> Result<L, R> liftRight(R value) {
    return new Right<L, R>(value);
  }

  public static <L, R> Result<L, R> lift(R value) {
    return new Right<L, R>(value);
  }

  public static <L, R> Result<L, R> liftLeftRight(Supplier<Boolean> p, Supplier<L> l, Supplier<R> r) {
    return p.get() ? Result.liftLeft(l.get()) : Result.liftRight(r.get());
  }
  
  public static <L, R> Result<L, R> liftLeftRightNotNullLeftBias(Supplier<L> l, Supplier<R> r) {
    L x = l.get();
    return x != null ? Result.liftLeft(x) : Result.liftRight(r.get());
  }
  
  public static <L, R> Result<L, R> liftLeftRightNotNullRightBias(Supplier<L> l, Supplier<R> r) {
    R x = r.get();
    return x != null ? Result.liftRight(x) : Result.liftLeft(l.get());
  }


  public static <L extends Throwable, R> Result<L, R> trying(Effect<R> supplier) {
    try {
      R r = supplier.get();
      return liftRight(r);
    } catch (Throwable t) {
      return liftLeft(Cast.cast(t));
    }
  }

  public abstract boolean isLeft();

  public abstract Option<L> leftValue();

  public abstract Option<R> rightValue();

  public R getOrElse(Supplier<R> orElse) {
    return rightValue().getOrElse(orElse);
  };

  public Result<L, R> recover(Supplier<R> r) {
    if (isRight())
      return this;
    return liftRight(r.get());
  }

  public Result<L, R> recoverM(Supplier<Result<L, R>> r) {
    if (isRight()) {
      return this;
    }
    return r.get();
  }

  public boolean isRight() {
    return !isLeft();
  }

  public <L1> Result<L1, R> mapLeft(Function<L, L1> func) {
    return Bias.leftBias(this).leftMap(func).toResult();
  }

  public <R1> Result<L, R1> mapRight(Function<R, R1> func) {
    return Bias.rightBias(this).rightMap(func).toResult();
  }

  public <R1> Result<L, R1> map(Function<R, R1> func) {
    return mapRight(func);
  }

  public static <L, A, B> Result<L, B> map(Result<L, A> resultA, Function<A, B> func) {
    return resultA.map(func);
  }

  public static <L, R> Result<L, R> flatten(Result<L, Result<L, R>> x) {
    if (x.isLeft())
      return Result.liftLeft(x.leftValue().unsafeGet());
    return x.rightValue().unsafeGet();
  }

  public static <L, R1, R2, R3> Result<L, R3> or2(final Result<L, R1> result1, final Result<L, R2> result2,
      Function2<R1, R2, R3> func, Function<R1, R3> f1, Function<R2, R3> f2) {
    if (result1.isRight() && result2.isRight()) {
      return Result
          .lift(result1.rightValue().flatMap(r1 -> result2.rightValue().map(r2 -> func.apply(r1, r2))).unsafeGet());
    }
    if (result1.isRight() && result2.isLeft()) {
      return Result.lift(f1.apply(result1.rightValue().unsafeGet()));
    }
    if (result2.isRight() && result1.isLeft()) {
      return Result.lift(f2.apply(result2.rightValue().unsafeGet()));
    }
    return Result.liftLeft(result1.leftValue().unsafeGet());
  }

  public <R1> Result<L, R1> flatMap(Function<R, Result<L, R1>> func) {
    return flatten(map(func));
  }

  public static <L, A, B, C> Result<L, C> map2(Result<L, A> resultA, Result<L, B> resultB, Function2<A, B, C> func) {
    return resultA.flatMap(a -> {
      return resultB.map(b -> func.apply(a, b));
    });
  }

  public <R1, R2> Result<L, R2> map2(Result<L, R1> resultR1, Function2<R, R1, R2> func) {
    return Result.map2(this, resultR1, func);
  }

  public static <L, A, B, C, D> Result<L, D> map3(Result<L, A> resultA, Result<L, B> resultB, Result<L, C> resultC,
      Function3<A, B, C, D> func) {
    return resultA.flatMap(a -> resultB.flatMap(b -> resultC.map(c -> func.apply(a, b, c))));
  }

  public <R1, R2, R3> Result<L, R3> map3(Result<L, R1> resultR1, Result<L, R2> resultR2,
      Function3<R, R1, R2, R3> func) {
    return Result.map3(this, resultR1, resultR2, func);
  }

  public static <L, A, B, C, D, E> Result<L, E> map4(Result<L, A> resultA, Result<L, B> resultB, Result<L, C> resultC,
      Result<L, D> resultD, Function4<A, B, C, D, E> func) {
    return resultA.flatMap(a -> resultB.flatMap(b -> resultC.flatMap(c -> resultD.map(d -> func.apply(a, b, c, d)))));
  }

  public <R1, R2, R3, R4> Result<L, R4> map4(Result<L, R1> resultR1, Result<L, R2> resultR2, Result<L, R3> resultR3,
      Function4<R, R1, R2, R3, R4> func) {
    return Result.map4(this, resultR1, resultR2, resultR3, func);
  }

  public static <L, A, B, C, D, E, F> Result<L, F> map5(Result<L, A> resultA, Result<L, B> resultB,
      Result<L, C> resultC, Result<L, D> resultD, Result<L, E> resultE, Function5<A, B, C, D, E, F> func) {
    return resultA.flatMap(a -> resultB
        .flatMap(b -> resultC.flatMap(c -> resultD.flatMap(d -> resultE.map(e -> func.apply(a, b, c, d, e))))));
  }

  public <R1, R2, R3, R4, R5> Result<L, R5> map5(Result<L, R1> resultR1, Result<L, R2> resultR2, Result<L, R3> resultR3,
      Result<L, R4> resultR4, Function5<R, R1, R2, R3, R4, R5> func) {
    return Result.map5(this, resultR1, resultR2, resultR3, resultR4, func);
  }

  private static class Left<L, R> extends Result<L, R> {
    private static final long serialVersionUID = 1L;
    private final Option<L> value;

    private Left(L value) {
      this.value = Option.lift(value);
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      Left<?, ?> left = (Left<?, ?>) obj;
      return value.equals(left.value);
    }

    @Override
    public String toString() {
      return "Left(" + value.toString() + ")";
    }

    @Override
    public Option<L> leftValue() {
      return value;

    }

    @Override
    public Option<R> rightValue() {
      return Option.none();
    }
  }

  private static class Right<L, R> extends Result<L, R> {
    private static final long serialVersionUID = 1L;
    private final Option<R> value;

    private Right(R value) {
      this.value = Option.lift(value);
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      Right<?, ?> right = (Right<?, ?>) obj;
      return value.equals(right.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public String toString() {
      return "Right(" + value.toString() + ")";
    }

    @Override
    public Option<L> leftValue() {
      return Option.none();
    }

    @Override
    public Option<R> rightValue() {
      return value;
    }
  }

  public static class Bias<L, R> {
    private Option<L> leftBias = Option.none();
    private Option<R> rightBias = Option.none();
    private Result<L, R> raw;

    private Bias() {
    }

    public Result<L, R> toResult() {
      return raw;
    }

    private static <L, R> Bias<L, R> leftBias(Result<L, R> result) {
      Bias<L, R> x = new Bias<>();
      x.raw = result;
      if (result.isLeft()) {
        Left<L, R> left = (Left<L, R>) result;
        x.leftBias = left.value;
      }
      return x;
    }

    private static <L, R> Bias<L, R> rightBias(Result<L, R> result) {
      Bias<L, R> x = new Bias<>();
      x.raw = result;
      if (result.isRight()) {
        Right<L, R> right = (Right<L, R>) result;
        x.rightBias = right.value;
      }
      return x;
    }

    public <R1> Bias<L, R1> rightMap(Function<R, R1> func) {
      if (raw.isRight()) {
        Option<R1> r1Opt = rightBias.map(func);
        // r1Opt maybe None
        return rightBias(Result.liftRight(r1Opt.isSome() ? r1Opt.unsafeGet() : null));
      }
      Left<L, R> left = (Left<L, R>) this.raw;

      Result<L, R1> result = left.value.isSome() ? Result.liftLeft(left.value.unsafeGet()) : Result.liftLeft(null);
      return leftBias(result);
    }

    public <L1> Bias<L1, R> leftMap(Function<L, L1> func) {
      if (leftBias.isSome()) {
        Option<L1> l1Opt = leftBias.map(func);
        // l1Opt maybe None
        return leftBias(Result.liftLeft(l1Opt.isSome() ? l1Opt.unsafeGet() : null));
      }
      Right<L, R> right = (Right<L, R>) this.raw;
      Result<L1, R> result = right.value.isSome() ? Result.liftRight(right.value.unsafeGet()) : Result.liftRight(null);
      return rightBias(result);
    }

  }
}