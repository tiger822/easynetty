package com.freestyle.netty.server;

import com.freestyle.protobuf.proto.MessageOuterClass;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;

import java.util.function.Supplier;

/**
 * Created by rocklee on 2022/1/21 17:29
 */
public class ProtobufMessageServer<T> extends GeneralServer implements com.freestyle.netty.server.intefaces.IProtobufMessageServer<T> {
  public ProtobufMessageServer(int port) {
    super(port);
  }

  public ProtobufMessageServer(EventLoopGroup bossGroup, EventLoopGroup workGroup, int port, LogLevel logLevel) {
    super(bossGroup, workGroup, port, logLevel);
  }
  private ChannelInitializer<SocketChannel> channelChannelInitializer=null;
  @Override
  public void run(Supplier<SimpleChannelInboundHandler<T>> supplierHandler) throws InterruptedException{
    if (channelChannelInitializer==null){
      channelChannelInitializer=new ChannelInitializer<SocketChannel>(){
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          ChannelPipeline pipeline = ch.pipeline();
          pipeline.addLast(new ProtobufVarint32FrameDecoder());//Protobuf帧解码器（粘包）
          pipeline.addLast(new ProtobufDecoder(MessageOuterClass.Message.getDefaultInstance()));
          pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
          pipeline.addLast(new ProtobufEncoder());
          pipeline.addLast(supplierHandler.get());//new instance
        }
      };
    }
    serverBootstrap.childHandler(channelChannelInitializer);
    ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
    channelFuture.channel().closeFuture().sync();
  }
}
