package com.freestyle.netty.easynetty.codes;

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
  private byte[] header;
  private Function<byte[],T> onCreateObject;
  private boolean throwExceptionOnInvalidFormat=false;
  private String decodeName;
  public CustomFrameDecoder(byte[] header, Function<byte[],T> onCreateObject) {
    this.header = header;
    this.onCreateObject=onCreateObject;
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
    ByteBuf b=in.retainedSlice();
    in.clear();
    out.add(b);
  }
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       int byteSize = in.readableBytes(); //这次有多少
       if (byteSize < 7) {//不足以形成一帧，则退出
         return;
       } else {
         //尝试看这篮子里面的数据够不够一帧
         in.markReaderIndex();
         byte headerSize = in.readByte();
         if (headerSize != header.length) {
           if (throwExceptionOnInvalidFormat) {
             throw new Exception("Invalid frame format");
           }
           else{
             packRawData(ctx,in,out);
             return;
           }
         }
         byte[] inHeader = new byte[headerSize];
         in.readBytes(inHeader);
         if (!Arrays.equals(inHeader, header)) {
           if (throwExceptionOnInvalidFormat) {
             throw new Exception("Invalid frame format");
           }
           else{
             packRawData(ctx,in,out);
             return;
           }
         }
         int dataLength = in.readInt();
         if (in.readableBytes() <  dataLength) {//不够一帧
           packRawData(ctx,in,out);
           return;
         } else {
           byte[] data=new byte[dataLength];
           in.readBytes(data);
           T obj=onCreateObject.apply(data);
           out.add(obj);
           return;
         }
     }

  }
}
