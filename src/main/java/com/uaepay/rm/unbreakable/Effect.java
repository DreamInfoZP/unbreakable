package com.uaepay.rm.unbreakable;

public interface Effect<A> {
  A get() throws Throwable;
}