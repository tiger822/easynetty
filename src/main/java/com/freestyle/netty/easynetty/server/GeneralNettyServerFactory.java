package com.freestyle.netty.easynetty.server;

import com.freestyle.netty.easynetty.server.interfaces.IGeneralServer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/24 11:37
 */
public class GeneralNettyServerFactory extends AbstractNettyServerFactory {
  @Override
  public IGeneralServer getGeneralServer(int port) {
    return new GeneralServer(port);
  }

  @Override
  public IGeneralServer getGeneralServer(EventLoopGroup bossGroup, EventLoopGroup workGroup, int port, LogLevel logLevel) {
    return new GeneralServer(bossGroup,workGroup,port,logLevel);
  }
}
