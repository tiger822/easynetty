package com.freestyle.netty.easynetty.lock;

import com.freestyle.netty.easynetty.lock.interfaces.PromiseUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞工具类
 * Created by rocklee on 2022/1/29 10:38
 */
public class CountDownPromiseUtil<T> extends AbstractPromiseUtil<T> implements PromiseUtil<T> {
  private final ConcurrentHashMap<Object, CountDownLatch> lockMaps=new ConcurrentHashMap<>();

  /***
   * 派出新的ID，用完必须第一时间归还
   */
  @Override
  public  Long newLock(Class<?>... tClass){
    if (localLong.get()!=null){
      throw new IllegalStateException("当前锁ID未归还");
    }
    long id=getId();
    localLong.set(id);
    Class<?> cl=tClass.length==0?Object.class:tClass[0];
    lockMaps.put(id+cl.getName(),new CountDownLatch(1));
    return id;
  }

  /**
   * 等待完成信息,默认等待10秒
   * @param milliSecondsToWait 单位毫秒
   */
  @Override
  public  T await(long... milliSecondsToWait){
    return await(getCurrentID(), milliSecondsToWait);
  }



  /**
   * 等待完成信息,默认等待10秒
   * @param id
   * @param milliSecondsToWait 单位毫秒
   */
  @Override
  public  T await(Long id, long... milliSecondsToWait) {
    return await(Object.class,id,milliSecondsToWait);
  }
  @Override
    public T await(Class<?> tClass,Long id, long... milliSecondsToWait) {
    try {
      Object key=id+tClass.getName();
      lockMaps.get(key).await(milliSecondsToWait.length==0?20000:milliSecondsToWait[0], TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return returnValues.get(id);
  }

  @Override
  public T await(Class<?> tClass,long... milliSecondsToWait) {
    return await(tClass,getCurrentID(),milliSecondsToWait);
  }

  /***
   * 发送完成信息
   * @param id
   */
  @Override
  public  boolean signal(Long id,T val){
    CountDownLatch c=lockMaps.get(id+val.getClass().getName());
    if (c==null)return false;
    returnValues.put(id,val);
    c.countDown();
    return true;
  }
  @Override
  public  void release(Class<?> ... tClass){
     Long id=getCurrentID();
     Class<?>cl=tClass.length==0?Object.class:tClass[0];
     Object key=id+cl.getName();
     if (lockMaps.containsKey(key)) {
      lockMaps.remove(key);
     }
     localLong.remove();
     if (returnValues.containsKey(id)) {
       returnValues.remove(id);
     }
  }


}
