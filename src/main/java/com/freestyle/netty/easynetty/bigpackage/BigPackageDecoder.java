package com.freestyle.netty.easynetty.bigpackage;

import com.freestyle.netty.easynetty.codes.AbstractMultipleDecode;
import com.freestyle.netty.easynetty.common.Utils;
import com.freestyle.netty.easynetty.dto.BigPackageConsts;
import com.freestyle.netty.easynetty.dto.BigPackageProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * Created by rocklee on 2022/2/21 17:07
 */
public abstract class BigPackageDecoder extends AbstractMultipleDecode {
  private BigPackageProperties properties;
  public BigPackageDecoder() {
    this( BigPackageConsts.protocols.keySet().iterator().next());
  }
  public abstract void onPackageOutput(ChannelHandlerContext ctx,BigPackageProperties properties,byte[] data,List<Object> out);
  public BigPackageDecoder(byte[] type) {
    registerClass(type, BigPackageProperties.class);
  }
  @Override
  @SuppressWarnings("deprecation")
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
          throws Exception {
    super.exceptionCaught(ctx,cause);
    cause.printStackTrace();
  }
  @Override
  public Object decodeObject(byte[] data, Class<?> tClass) {
    return Utils.fromJsonBytes(data,BigPackageProperties.class);
  }
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    if (properties==null) {//开始
      Object object = decode(ctx, msg);
      if (object != null) {
        if (object instanceof BigPackageProperties) {
          if (properties != null) {//又收到文件描述？？
            return;
          }
          properties = (BigPackageProperties) object;
          onPackageOutput(ctx,properties,null,out);
        }
      }
    }
    else { //已经有头部描述
      int bufLen= msg.readableBytes();
      /*if (properties.getRt()+bufLen<=properties.getTotal()) {
        properties.setRt(properties.getRt() + bufLen);
        onPackageOutput(ctx,properties, ByteBufUtil.getBytes(msg),out);
        msg.clear();
        return;
      }*/
      ByteBuf dumpBuf=msg.readRetainedSlice((int) (bufLen<=properties.getTotal()-properties.getRt()
              ?bufLen:properties.getTotal()-properties.getRt()));
      try {
        properties.setRt(properties.getRt() + dumpBuf.readableBytes());
        onPackageOutput(ctx,properties,ByteBufUtil.getBytes(dumpBuf),out);
        if (properties.getTotal().longValue()==properties.getRt()){
          properties=null;
        }
      }
      finally {
        ReferenceCountUtil.release(dumpBuf);
      }
    }
  }
}
