package com.freestyle.netty.easynetty.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelPipeline;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocklee on 2022/2/18 14:02
 */
public class NettyUtil {
  private static final ConcurrentHashMap<ChannelPipeline,ByteBuf> lastByteBuf=new ConcurrentHashMap<>();
  private static AttributeKey<ByteBuf> byteBufAttributeKey=AttributeKey.valueOf(ByteBuf.class,"byteBufKey");
  /***
   * 重新将原始数据包投递
   * @param pipeline
   * @param data
   */
  public static void reDeliver(ChannelPipeline pipeline, Object data){
    System.out.println("reDeliver:"+pipeline.channel().hashCode()+","+data);
    if (!(data instanceof ByteBuf)) return;
    ByteBuf byteBuf=(ByteBuf)data;
    /*
    Attribute<ByteBuf> attr=pipeline.channel().attr(byteBufAttributeKey);
    ByteBuf lastBuf=attr.get();*/
    ByteBuf lastBuf=lastByteBuf.get(pipeline);
    //byteBuf.markReaderIndex();
    try {
      if (lastBuf != null ) {
        if (byteBuf.equals(lastBuf))
        return;
        byte[] a= ByteBufUtil.getBytes(byteBuf.slice());
        byte[] b= ByteBufUtil.getBytes(lastBuf.slice());
        if (Arrays.equals(a,b))
          return;
      }
    }
    finally {
     // byteBuf.resetReaderIndex();
    }
    if (lastBuf!=null){
      lastBuf.release();
    }
    lastByteBuf.put(pipeline,byteBuf.retainedSlice());
    pipeline.fireChannelRead(byteBuf.retain());
  }
}
