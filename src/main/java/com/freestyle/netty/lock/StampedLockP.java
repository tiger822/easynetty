package com.freestyle.netty.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by rocklee on 2022/1/29 14:56
 */
public class StampedLockP extends StampedLock {
  private volatile long writeLock=0;

  public void lock(){
    writeLock=writeLock();
  }
  public synchronized void unLock(){
    if (writeLock!=0) {
      unlockWrite(writeLock);
    }
    writeLock=0;
  }
  public boolean await(long time, TimeUnit timeUnit) throws InterruptedException {
     long l= tryReadLock(time,timeUnit);
     if (validate(l)){
       unlockRead(l);
       return true;
     }
    {
      System.out.println("W:"+writeLock);
    }
     return false;
  }
  public long getLock() {
    return writeLock;
  }
}
