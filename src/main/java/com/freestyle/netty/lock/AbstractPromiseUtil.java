package com.freestyle.netty.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by rocklee on 2022/1/29 14:50
 */
public class AbstractPromiseUtil<T> {
  private final static AtomicLong idGenerator=new AtomicLong(0);
  protected final ThreadLocal<Long> localLong=new ThreadLocal<Long>(){};
  protected final ConcurrentHashMap<Long, T> returnValues=new ConcurrentHashMap<>();

  //获取顺序ID
  protected long getId(){
    return idGenerator.incrementAndGet();
  }

  public  Long getCurrentID(){
    Long id=localLong.get();
    if (id==null){
      throw new IllegalStateException("当前锁ID为空");
    }
    return id;
  }
}
