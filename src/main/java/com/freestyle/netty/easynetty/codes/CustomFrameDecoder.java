package com.freestyle.netty.easynetty.codes;

import com.freestyle.netty.easynetty.common.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Please use AbstractMultipleDecode
 * Created by rocklee on 2022/1/25 14:40
 * 自定义帧：header-length(byte)+header(byte[])+data-length(int)+data(byte[]);
 */
@Deprecated
public class CustomFrameDecoder<T> extends ByteToMessageDecoder {
  public static int MAX_FRAME_SIZE=65536;
  private int header;
  private Function<byte[],T> onCreateObject;
  private boolean throwExceptionOnInvalidFormat=false;
  private String decodeName;
  private boolean reDeliverRawData;
  public CustomFrameDecoder(int header, Function<byte[],T> onCreateObject) {
    this.header = header;
    this.onCreateObject=onCreateObject;
  }
  public CustomFrameDecoder setReDeliverRawData(boolean reDeliverRawData){
    this.reDeliverRawData=reDeliverRawData;
    return this;
  }
  public CustomFrameDecoder<T> setDecodeName(String decodeName){
    this.decodeName=decodeName;
    return this;
  }
  public CustomFrameDecoder setThrowExceptionOnInvalidFormat(boolean throwExceptionOnInvalidFormat){
    this.throwExceptionOnInvalidFormat=throwExceptionOnInvalidFormat;
    return this;
  }
  private void packRawData(ChannelHandlerContext ctx,ByteBuf in,List<Object>out){
    in.resetReaderIndex();
    if (reDeliverRawData){//最后一个解码器，则重新投入，让前面的解码器尝试解码
      NettyUtil.reDeliver(ctx.pipeline(),in);
    }
    else{
      ByteBuf cloneBuf=in.retainedSlice();
      in.clear();
      out.add(cloneBuf);
    }
  }
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       int byteSize = in.readableBytes(); //这次有多少
       if (byteSize < 8) {//不足以形成一帧，则退出
         return;
       } else {
         //尝试看这篮子里面的数据够不够一帧
         in.markReaderIndex();
         int dataLength = in.readInt();
         if (dataLength>MAX_FRAME_SIZE||dataLength<0){//不是本类型数据
           packRawData(ctx,in,out);
           return;
         }
         if (in.readableBytes() <  dataLength+4) {//不够一帧
           in.resetReaderIndex();
           return;
         }
         else if (in.readInt()!=header){ //不是本类型数据
           packRawData(ctx,in,out);
           return;
         }
         else {
           byte[] data=new byte[dataLength];
           in.readBytes(data);
           T obj=onCreateObject.apply(data);
           out.add(obj);
           return;
         }

         /*byte headerSize = in.readByte();
         if (headerSize != header.length) {
           packRawData(ctx,in,out);
           return ;
         }
         byte[] inHeader = new byte[headerSize];
         in.readBytes(inHeader);
         if (!Arrays.equals(inHeader, header)) {
           packRawData(ctx,in,out);
           return;
         }
         int dataLength = in.readInt();
         if (in.readableBytes() <  dataLength) {//不够一帧
           in.resetReaderIndex();
           return;
         } else {
           byte[] data=new byte[dataLength];
           in.readBytes(data);
           T obj=onCreateObject.apply(data);
           out.add(obj);
           return;
         }*/
     }

  }
}
