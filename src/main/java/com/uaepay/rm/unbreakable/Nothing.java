package com.uaepay.rm.unbreakable;

public abstract class Nothing {
  private Nothing(){}
  public static Nothing instance = new Nothing(){};
}