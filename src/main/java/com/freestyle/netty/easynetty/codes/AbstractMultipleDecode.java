package com.freestyle.netty.easynetty.codes;

import com.freestyle.netty.easynetty.common.NettyUtil;
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
  private boolean reDeliverRawData;
 // private final AttributeKey<byte[]> lastDataKey=AttributeKey.valueOf(byte[].class,"lastDataKey");
  public abstract Object decodeObject(byte[] data,Class<?> tClass);

  public AbstractMultipleDecode setReDeliverRawData(boolean reDeliverRawData){
    this.reDeliverRawData=reDeliverRawData;
    return this;
  }

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
  /*private void packRawData(ChannelHandlerContext ctx,ByteBuf in,List<Object>out){
    in.resetReaderIndex();
    ByteBuf b=in.retainedSlice();
    in.clear();
    out.add(b);
  }*/
  private Class recvClass; //正在接收的类
  private long frameSize=-1;//当前这一帧的长度
  private ByteBuf cumulation; //累积下来的原始数据
  private boolean first;
  private Cumulator cumulator = MERGE_CUMULATOR;
  private Object packRawData(ChannelHandlerContext ctx,ByteBuf in){
    in.resetReaderIndex();
    if (reDeliverRawData){//最后一个解码器，则重新投入，让前面的解码器尝试解码
      NettyUtil.reDeliver(ctx.pipeline(),in);
      return null;
    }
    else{
      ByteBuf cloneBuf=in.retainedSlice();
      in.clear();
      return cloneBuf;
    }
  }
  protected Object decode(ChannelHandlerContext ctx,ByteBuf in ){
    int byteSize=in.readableBytes();
    if (byteSize<6){
      return null;
    }
    in.markReaderIndex();
    if (recvClass==null){//新的一个循环
      //先确定数据类型
      for (Map.Entry<byte[],Class<?>>entry:classMap.entrySet()) {
        in.resetReaderIndex();
        byte headerSize = in.readByte();
        if (headerSize != entry.getKey().length) {
          continue;
        }
        byte[] inHeader = new byte[entry.getKey().length];
        in.readBytes(inHeader);
        if (!Arrays.equals(inHeader, entry.getKey())) {
          continue;
        }
        recvClass=entry.getValue();
        break;
      }
      if (recvClass==null){//数据不匹配，装起来扔到下个handler
        return packRawData(ctx,in);
        /*in.resetReaderIndex();
        if (reDeliverRawData){//最后一个解码器，则重新投入，让前面的解码器尝试解码
          NettyUtil.reDeliver(ctx.pipeline(),in);
          return null;
        }
        else{
          ByteBuf cloneBuf=in.retainedSlice();
          in.clear();
          return cloneBuf;
        }*/
      }
      return null;
    }
    else if (frameSize==-1){
      if (in.readableBytes()<4){//数据不够一帧，退出
        return null;
      }
      frameSize =in.readInt();//此类的长度
      if (frameSize<=0){ //数据不匹配
        return packRawData(ctx,in);
      }
      return null;
    }
    else{//已经知道要接收的是什么类型的数据，就接收剩下的数据吧
      if (in.readableBytes() < frameSize) {//不够一帧
        return null;
      }
      try {
        byte[] data = new byte[(int) frameSize];
        in.readBytes(data);
        Object obj = decodeObject(data, recvClass);
        return obj;
      }
      finally {
        frameSize =-1;
        recvClass = null;
      }
    }
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    Object object=decode(ctx,msg);
    if (object!=null){
       out.add(object);
    }

  }
}
