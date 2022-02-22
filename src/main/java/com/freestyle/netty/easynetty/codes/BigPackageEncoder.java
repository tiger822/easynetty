package com.freestyle.netty.easynetty.codes;

import com.freestyle.netty.easynetty.common.Utils;
import com.freestyle.netty.easynetty.dto.BigPackageConsts;
import com.freestyle.netty.easynetty.dto.BigPackageProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Function;

/**
 * Created by rocklee on 2022/2/16 21:56
 */
public class BigPackageEncoder extends CustomFrameEncoder<BigPackageProperties>{
  public BigPackageEncoder() {
    this(BigPackageConsts.protocols.keySet().iterator().next());
  }
  public BigPackageEncoder(int header) {
    this(header,properties -> Utils.toJsonBytes(properties));
  }
  public BigPackageEncoder(int header, Function<BigPackageProperties, byte[]> converter) {
    super(BigPackageProperties.class,header,converter);
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, BigPackageProperties properties, ByteBuf out) throws Exception {
    /*out.writeByte((byte)(getHeader().length));
    out.writeBytes(getHeader());*/
    byte[] bytes=getConverter().apply(properties);
    out.writeInt(bytes.length);
    out.writeInt(getHeader());
    out.writeBytes(bytes);
  }
}
