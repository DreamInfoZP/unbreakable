package com.uaepay.rm.unbreakable;

@FunctionalInterface
public interface Function4<A, B, C, D, E> {
  E apply(A a, B b, C c, D d);
}