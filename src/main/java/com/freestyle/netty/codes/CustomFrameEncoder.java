package com.freestyle.netty.codes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.function.Function;

/**
 * Created by rocklee on 2022/1/25 15:29
 */
public class CustomFrameEncoder<T> extends MessageToByteEncoder<T> {
  private byte[] header;
  private Function<T,byte[]> converter;

  public byte[] getHeader() {
    return header;
  }

  public CustomFrameEncoder<T> setHeader(byte[] header) {
    this.header = header;
    return  this;
  }

  public Function<T, byte[]> getConverter() {
    return converter;
  }

  public CustomFrameEncoder(Class<? extends T> outboundMessageType, byte[] header, Function<T, byte[]> converter) {
    super(outboundMessageType);
    this.header = header;
    this.converter = converter;
  }

  public CustomFrameEncoder() {
  }

  public CustomFrameEncoder<T> setConverter(Function<T, byte[]> converter) {
    this.converter = converter;
    return this;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
      out.writeByte(header.length);
      out.writeBytes(header);
      byte[] bytes=converter.apply(msg);
      out.writeInt(bytes.length);
      out.writeBytes(bytes);
  }
}
