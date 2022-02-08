package com.freestyle.netty.client;

import com.freestyle.netty.client.interfaces.IGeneralClient;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/24 11:16
 */
public abstract class AbstractNettyClientFactory {
  public abstract IGeneralClient getClient(String host,int port);
  public abstract IGeneralClient getClient(EventLoopGroup bossGroup, String host, int port, LogLevel logLevel);
}
