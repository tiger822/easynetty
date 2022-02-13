package com.freestyle.netty.easynetty.lock;

import com.freestyle.netty.easynetty.lock.interfaces.PromiseUtil;

import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by rocklee on 2022/1/29 14:40
 */
public class StampedLockPromiseUtil<T> extends AbstractPromiseUtil<T> implements PromiseUtil<T> {
  private final ConcurrentHashMap<Object, StampedLockP> lockMaps=new ConcurrentHashMap<>();
  private final Stack<StampedLockP> lockQueue;


  public StampedLockPromiseUtil(int... poolSize) {
     int size=poolSize.length==0?50:poolSize[0];
     lockQueue =new Stack<>();
     for (int i=0;i<size;i++){
       lockQueue.push(new StampedLockP());
     }
  }
  public int getLockPoolSize(){
    return lockQueue.size();
  }
  private StampedLockP getLock(){
    StampedLockP lock;
    lock = lockQueue.pop();
    if (lock == null) {
        synchronized (this) {
          lockQueue.push(new StampedLockP());
          lock = lockQueue.pop();
          return lock;
        }
    }
    return lock;
  }
  private void returnLock(StampedLockP lock){
    lockQueue.push(lock);
  }


  @Override
  public  Long newLock(Class<?>... tClass){
    if (localLong.get()!=null){
      throw new IllegalStateException("当前锁ID未归还");
    }
    long id=getId();
    localLong.set(id);
    StampedLockP lock=getLock();
    lock.lock();
    Class<?> cl=tClass.length==0?Object.class:tClass[0];
    lockMaps.put(id+cl.getName(),lock);
    return id;
  }


  @Override
  public T await(long... microseSecondsToWait) {
    return await(Object.class, getCurrentID(), microseSecondsToWait);
  }

  @Override
  public T await(Class<?> tClass, long... microseSecondsToWait) {
    return await(tClass,getCurrentID(),microseSecondsToWait);
  }

  @Override
  public T await(Long id, long... microseSecondsToWait){
    return await(Object.class,id,microseSecondsToWait);
  }

  @Override
  public T await(Class<?> tClass,Long id, long... microseSecondsToWait) {
    T ret=null;
    Object key=id+tClass.getName();
    try {
      if (lockMaps.get(key).await(microseSecondsToWait.length==0?20000:microseSecondsToWait[0], TimeUnit.MILLISECONDS)){
        ret= returnValues.get(id);
      }
      else{
        ret= returnValues.get(id);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return ret;
  }


  @Override
  public boolean signal(Long id, T val) {
    StampedLockP lock=lockMaps.get(id+val.getClass().getName());
    if (lock==null)return false;
    returnValues.put(id,val);
    lock.unLock();
    return true;
  }

  @Override
  public void release(Class<?>... tClass) {
    Long id=getCurrentID();
    Class<?>cl=tClass.length==0?Object.class:tClass[0];
    Object key=id+cl.getName();
    StampedLockP lock=lockMaps.get(key);
    if (lock!=null) {
      lock.unLock();
      returnLock(lock);
      lockMaps.remove(key);
    }
    localLong.remove();
    if (returnValues.containsKey(id)) {
      returnValues.remove(id);
    }
  }
}
