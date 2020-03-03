package com.uaepay.rm.unsafe;

/**
 * 不安全的类型强转
 */
public class Cast {
  @SuppressWarnings("unchecked")
  public static <T> T cast(Object obj) {
    return (T) obj;
  }
}