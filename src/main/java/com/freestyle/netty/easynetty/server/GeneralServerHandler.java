package com.freestyle.netty.easynetty.server;

import com.freestyle.protobuf.proto.MessageOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by rocklee on 2022/1/20 16:47
 */
public class GeneralServerHandler extends SimpleChannelInboundHandler<MessageOuterClass.Message> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MessageOuterClass.Message msg) throws Exception {
   // System.out.println("channelRead0:");
    MessageOuterClass.MessageProperties messageProperties=msg.getProperties();
    //String jsonText= JsonFormat.printer().print(messageProperties);
    //System.out.println(jsonText);
   // MessageProperties messageProperties1= Utils.fromJsonBytes(jsonText.getBytes("UTF-8"),MessageProperties.class);
   // System.out.println(messageProperties1);
    messageProperties=messageProperties.toBuilder().setId(999).build();
    msg=msg.toBuilder().setProperties(messageProperties).build();
    ctx.channel().writeAndFlush(msg);
  }
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    String clientName = ctx.channel().remoteAddress().toString();
    System.out.println("RemoteAddress:"+clientName+" active!");
    super.channelActive(ctx);
  }
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent){
      IdleStateEvent event = (IdleStateEvent)evt;
      /*if (event.state().equals(IdleState.READER_IDLE)){
        //空闲4s后触发
        if (counter>=10){
          ctx.channel().close().sync();
          String clientName = ctx.channel().remoteAddress().toString();
          System.out.println(""+clientName+"offline");
          nettyServer.removeClient(clientName);
          //判断是否有在线的
          if (nettyServer.getClientMapSize()){
            return;
          }
        }else{
          counter++;
          System.out.println("loss"+counter+"count HB");
        }
      }*/
    }
  }
}
