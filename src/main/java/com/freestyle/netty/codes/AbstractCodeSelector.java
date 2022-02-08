package com.freestyle.netty.codes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 编码选择器
 * Created by rocklee on 2022/1/26 13:44
 */
public abstract class AbstractCodeSelector extends ByteToMessageDecoder {

  public abstract ChannelInboundHandlerAdapter determineDecode(ByteBuf in);
  public abstract void addHandler(ChannelPipeline pipeline, ChannelInboundHandlerAdapter channelInboundHandlerAdapter);
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    ChannelInboundHandlerAdapter channelInboundHandlerAdapter=determineDecode(in);
    addHandler(ctx.pipeline(),channelInboundHandlerAdapter);
    if (channelInboundHandlerAdapter!=null){
      ctx.pipeline().remove(this);
    }

  }
}
