package com.freestyle.netty.easynetty.client;

import com.freestyle.netty.easynetty.client.interfaces.IProtobufMessageClient;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;

/**
 * Created by rocklee on 2022/1/24 11:29
 */
public class ProtobufMesssageNettyClientFactory<T> extends AbstractNettyClientFactory {
  @Override
  public IProtobufMessageClient getClient(String host, int port) {
    return new ProtobufMessageClient(host,port);
  }

  @Override
  public IProtobufMessageClient getClient(EventLoopGroup bossGroup, String host, int port, LogLevel logLevel) {
    return new ProtobufMessageClient<T>(bossGroup,host,port,logLevel);
  }
}
