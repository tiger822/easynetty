package com.freestyle.netty.server;

import com.freestyle.netty.server.intefaces.IGeneralServer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/24 11:36
 */
public abstract class AbstractNettyServerFactory {
  public abstract IGeneralServer getGeneralServer(int port);
  public abstract IGeneralServer getGeneralServer(EventLoopGroup bossGroup, EventLoopGroup workGroup, int port, LogLevel logLevel);
}
