package com.uaepay.rm.unbreakable;

@FunctionalInterface
public interface Function2<A, B, C> {
  C apply(A a, B b);
}