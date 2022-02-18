package com.freestyle.netty.easynetty.codes;

import com.freestyle.netty.easynetty.dto.BigPackageProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by rocklee on 2022/2/16 21:56
 */
public class BigPackageEncoder extends CustomFrameEncoder<BigPackageProperties>{
  @Override
  protected void encode(ChannelHandlerContext ctx, BigPackageProperties properties, ByteBuf out) throws Exception {
    out.writeByte((byte)(getHeader().length));
    out.writeBytes(getHeader());
    byte[] bytes=getConverter().apply(properties);
    out.writeInt(bytes.length);
    out.writeBytes(bytes);
  }
}
