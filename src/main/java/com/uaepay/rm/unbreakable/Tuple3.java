package com.uaepay.rm.unbreakable;

public final class Tuple3<T1, T2, T3> {
  public final T1 _1;
  public final T2 _2;
  public final T3 _3;

  private Tuple3(T1 _1, T2 _2, T3 _3) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> with(T1 t1, T2 t2, T3 t3) {
    return new Tuple3<>(t1, t2, t3);
  }

  public <T4> Tuple4<T1, T2, T3, T4> append(T4 t4) {
    return Tuple4.with(_1, _2, _3, t4);
  }
}