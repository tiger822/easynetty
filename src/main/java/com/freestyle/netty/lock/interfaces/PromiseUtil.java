package com.freestyle.netty.lock.interfaces;

/**
 * Created by rocklee on 2022/1/29 10:57
 */
public interface PromiseUtil<T> {
  Long newLock(Class<?>... tClass);
  Long getCurrentID();
  T await(long... microseSecondsToWait);
  T await(Class<?> tClass, long... microseSecondsToWait);
  T await(Long id, long... microseSecondsToWait);
  T await(Class<?> tClass,Long id, long... microseSecondsToWait);

  boolean signal(Long id,T val);
  void release(Class<?>... tClass);
}
