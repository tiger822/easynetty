package com.freestyle.netty.codes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocklee on 2022/2/8 20:18
 */
public abstract class AbstractMultipleDecode extends ByteToMessageDecoder {
  private final ConcurrentHashMap<byte[],Class<?>> classMap=new ConcurrentHashMap<>();
  public abstract Object decodeObject(byte[] data,Class<?> tClass);

  public AbstractMultipleDecode registerClass(byte[] headerTag, Class<?> tClass){
    if (classMap.containsKey(headerTag)||classMap.containsValue(tClass)){
      throw new IllegalArgumentException("headerTag/class has registered.");
    }
    classMap.put(headerTag,tClass);
    return this;
  }
  private void packRawData(ChannelHandlerContext ctx,ByteBuf in,List<Object>out){
    in.resetReaderIndex();
    ByteBuf b=in.retainedSlice();
    in.clear();
    out.add(b);
  }
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
      if (in.readableBytes() < dataLength) {//不够一帧
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
