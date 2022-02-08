package com.freestyle.netty.client;

import com.freestyle.netty.client.interfaces.IGeneralClient;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/24 11:22
 */
public class GeneralNettyClientFactory extends AbstractNettyClientFactory {

  @Override
  public IGeneralClient getClient(String host, int port) {
    return new GeneralClient(host,port);
  }

  @Override
  public IGeneralClient getClient(EventLoopGroup bossGroup, String host, int port, LogLevel logLevel) {
    return new GeneralClient(bossGroup,host,port,logLevel);
  }
}
