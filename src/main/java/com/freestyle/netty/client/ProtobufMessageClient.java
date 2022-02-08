package com.freestyle.netty.client;

import com.freestyle.protobuf.proto.MessageOuterClass;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/21 16:11
 */
public class ProtobufMessageClient<T> extends GeneralClient implements com.freestyle.netty.client.interfaces.IProtobufMessageClient<T> {
  public ProtobufMessageClient(String host, int port) {
    super(host, port);
  }
  @Override
  public void run(boolean waitForDisconnect, SimpleChannelInboundHandler<T> handler) throws InterruptedException {
    bootstrap.handler(new ChannelInitializer<SocketChannel>(){
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(MessageOuterClass.Message.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(handler);
      }
    });
    channelFuture = bootstrap.connect(host,port).sync();
    if (waitForDisconnect) {
      channelFuture.channel().closeFuture().sync();
    }
  }
  public ProtobufMessageClient(EventLoopGroup bossGroup, String host, int port, LogLevel logLevel) {
    super(bossGroup, host, port, logLevel);
  }
}
