package com.freestyle.netty.server;

import com.freestyle.netty.server.intefaces.IProtobufMessageServer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/24 11:38
 */
public class ProtobufMessageNettyServerFactory<T> extends AbstractNettyServerFactory {
  @Override
  public IProtobufMessageServer getGeneralServer(int port) {
    return new ProtobufMessageServer(port);
  }

  @Override
  public IProtobufMessageServer getGeneralServer(EventLoopGroup bossGroup, EventLoopGroup workGroup, int port, LogLevel logLevel) {
    return new ProtobufMessageServer<T>(bossGroup,workGroup,port,logLevel);
  }
}
