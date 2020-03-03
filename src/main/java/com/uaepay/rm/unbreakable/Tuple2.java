package com.uaepay.rm.unbreakable;

public final class Tuple2<T1, T2> {
  public final T1 _1;
  public final T2 _2;

  private Tuple2(T1 _1, T2 _2) {
    this._1 = _1;
    this._2 = _2;
  }

  public static <T1, T2> Tuple2<T1, T2> with(T1 t1, T2 t2) {
    return new Tuple2<>(t1, t2);
  }

  public <T3> Tuple3<T1, T2, T3> append(T3 t3) {
    return Tuple3.with(_1, _2, t3);
  }
}