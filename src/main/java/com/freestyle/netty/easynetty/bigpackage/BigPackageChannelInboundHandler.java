package com.freestyle.netty.easynetty.bigpackage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freestyle.netty.easynetty.codes.AbstractMultipleDecode;
import com.freestyle.netty.easynetty.codes.BigPackageEncoder;
import com.freestyle.netty.easynetty.common.Utils;
import com.freestyle.netty.easynetty.dto.BigPackageConsts;
import com.freestyle.netty.easynetty.dto.BigPackageProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;
import java.util.Map;

/**大包处理类
 * Created by rocklee on 2022/2/17 9:42
 */
public abstract class BigPackageChannelInboundHandler extends SimpleChannelInboundHandler {
  private final ThreadLocal<Map<ChannelHandlerContext,BigPackageProperties>> localProperties=new ThreadLocal<Map<ChannelHandlerContext,BigPackageProperties>>(){
    @Override
    protected Map<ChannelHandlerContext,BigPackageProperties> initialValue() {
      return new HashMap<>();
    }
  };
  private BigPackageProperties getBigPackageProperties(){
    if (ctx==null)return null;
    return localProperties.get().get(ctx);
  }
  private byte[] type;
  public BigPackageChannelInboundHandler(ChannelPipeline pipeline) {
    this(pipeline, BigPackageConsts.protocols.keySet().iterator().next());
  }
  public BigPackageChannelInboundHandler(ChannelPipeline pipeline,byte[] type) {
    super();
    this.type=type;
    BigPackageEncoder bigPackageEncoder=new BigPackageEncoder();
    bigPackageEncoder.setHeader(type);
    bigPackageEncoder.setConverter(Utils::toJsonBytes);
    pipeline.addLast(bigPackageEncoder);
    final TypeReference<BigPackageProperties> typeReference=new TypeReference<BigPackageProperties>() {
    };
    AbstractMultipleDecode decode=new AbstractMultipleDecode() {
      @Override
      public Object decodeObject(byte[] data, Class<?> tClass) {
        return Utils.fromJsonBytes(data,typeReference);
      }
    };
    decode.registerClass(type,BigPackageProperties.class);
    pipeline.addLast(decode);
  }
  @Override
  @SuppressWarnings("deprecation")
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
          throws Exception {
    super.exceptionCaught(ctx,cause);
    cause.printStackTrace();
  }
  private ChannelHandlerContext ctx;
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    localProperties.get().remove(ctx);
  }
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    localProperties.get().put(ctx,null);
  }
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    this.ctx=ctx;
    super.channelRead(ctx,msg);
  }
  @Override
  public boolean acceptInboundMessage(Object msg) throws Exception {
    BigPackageProperties properties=getBigPackageProperties();
    if (msg instanceof BigPackageProperties){
      if (properties!=null){ //已经开始了收数据又收到这个？？
        return false;
      }
      localProperties.get().put(ctx,(BigPackageProperties) msg); //开始新一个周期
      return true;
    }
    else{
      if (properties!=null){
        ByteBuf byteBuf=(ByteBuf)msg;
        int bufLen= byteBuf.readableBytes();
        if (properties.getRt()+bufLen<=properties.getTotal()) {
          properties.setRt(properties.getRt() + bufLen);
          properties.setByteBuf(byteBuf);
          return true;
        }
        else{ //和后面的包有粘连，要分开
          //ByteBuf dumpBuf=byteBuf.readSlice((int) (properties.getTotal()-properties.getRt()));
          ByteBuf dumpBuf=byteBuf.readRetainedSlice((int) (properties.getTotal()-properties.getRt()));
          try {
            properties.setByteBuf(dumpBuf);
            properties.setRt(properties.getRt() + dumpBuf.readableBytes());
            channelRead0(ctx, msg);
          }
          finally {
            ReferenceCountUtil.release(dumpBuf);
          }
          return false;
        }

      }
      else return false; //不是接收数据包状态则扔给下一处理方
    }
  }
  public abstract void onPackageOutput(ChannelHandlerContext ctx,BigPackageProperties properties,byte[] data);
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof BigPackageProperties){
      onPackageOutput(ctx,(BigPackageProperties) msg,null);
    }
    else {
      BigPackageProperties properties=getBigPackageProperties();
      try {
        byte[] data= ByteBufUtil.getBytes(properties.getByteBuf());
        //properties.setByteBuf(null);
        onPackageOutput(ctx,properties,data);
      }
      finally {
        if (properties.getTotal()==properties.getRt()) {
          localProperties.get().remove(ctx);
        }
      }
    }
  }
}
