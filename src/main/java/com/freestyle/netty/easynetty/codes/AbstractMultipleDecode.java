package com.freestyle.netty.easynetty.codes;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocklee on 2022/2/8 20:18
 */
public abstract class AbstractMultipleDecode extends ByteToMessageDecoder {
  private final ConcurrentHashMap<byte[],Class<?>> classMap=new ConcurrentHashMap<>();
 // private final AttributeKey<byte[]> lastDataKey=AttributeKey.valueOf(byte[].class,"lastDataKey");
  public abstract Object decodeObject(byte[] data,Class<?> tClass);

  public AbstractMultipleDecode registerClass(byte[] headerTag, Class<?> tClass){
    if (classMap.containsKey(headerTag)||classMap.containsValue(tClass)){
      throw new IllegalArgumentException("headerTag/class has registered.");
    }
    classMap.put(headerTag,tClass);
    return this;
  }
  public AbstractMultipleDecode registerClasses(Map<byte[],Class<?>> classesMap){
    for (Map.Entry<byte[], Class<?>> entry : classesMap.entrySet()) {
      registerClass(entry.getKey(),entry.getValue());
    }
    return this;
  }
  private void packRawData(ChannelHandlerContext ctx,ByteBuf in,List<Object>out){
    in.resetReaderIndex();
    ByteBuf b=in.retainedSlice();
    in.clear();
    out.add(b);
  }
  /*private byte[] combineBytes(byte[] a,byte[] b){
    byte[] ret=new byte[a.length+b.length];
    System.arraycopy(a,0,ret,0,a.length);
    System.arraycopy(b,0,ret,a.length,b.length);
    return ret;
  }
  private byte[] getLastData(ChannelHandlerContext cx){
    Attribute<byte[]> att=cx.channel().attr(lastDataKey);
    byte[] data=att.get();
    return data==null?new byte[]{}:data;
  }
  private void setLastDataKey(ChannelHandlerContext cx,byte[] data){
    Attribute<byte[]> att=cx.channel().attr(lastDataKey);
    att.set(data);
  }*/
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    int byteSize = in.readableBytes(); //这次有多少
    if (byteSize < 6) {//不足以形成一帧，则退出
      return;
    }
    in.markReaderIndex();
    for (Map.Entry<byte[],Class<?>>entry:classMap.entrySet()) {
      in.resetReaderIndex();
      //尝试看这篮子里面的数据够不够一帧
      byte headerSize = in.readByte();
      if (headerSize != entry.getKey().length) {
         continue;
      }
      byte[] inHeader = new byte[entry.getKey().length];
      in.readBytes(inHeader);
      if (!Arrays.equals(inHeader, entry.getKey())) {
        continue;
      }
      int dataLength = in.readInt();
      /*//by rocklee 18/Feb/2022
      byte[] lastData=getLastData(ctx);
      int available=in.readableBytes()+lastData.length;
      //by rocklee 18/Feb/2022 end*/
      if (in.readableBytes() < dataLength) {//不够一帧
        /*lastData=combineBytes(lastData, ByteBufUtil.getBytes(in));
        setLastDataKey(ctx,lastData);*/
        continue;
      } else {
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = decodeObject(data,entry.getValue());
        out.add(obj);
        break;
      }
    }
    if (out.size()==0){
      packRawData(ctx,in,out);
    }
  }
}
