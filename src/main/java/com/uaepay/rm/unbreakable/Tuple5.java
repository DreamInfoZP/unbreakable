package com.uaepay.rm.unbreakable;

public final class Tuple5<T1, T2, T3, T4, T5> {
  public final T1 _1;
  public final T2 _2;
  public final T3 _3;
  public final T4 _4;
  public final T5 _5;

  private Tuple5(T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;

  }

  public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> with(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
    return new Tuple5<>(t1, t2, t3, t4, t5);
  }
}