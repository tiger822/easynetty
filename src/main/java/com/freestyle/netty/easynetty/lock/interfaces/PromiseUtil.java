package com.freestyle.netty.easynetty.lock.interfaces;

/**
 * Created by rocklee on 2022/1/29 10:57
 */
public interface PromiseUtil<T> {
  Long newLock(Class<?>... tClass);
  Long getCurrentID();
  T await(long... milliSecondsToWait);
  T await(Class<?> tClass, long... milliSecondsToWait);
  T await(Long id, long... milliSecondsToWait);
  T await(Class<?> tClass,Long id, long... milliSecondsToWait);

  boolean signal(Long id,T val);
  void release(Class<?>... tClass);
}
