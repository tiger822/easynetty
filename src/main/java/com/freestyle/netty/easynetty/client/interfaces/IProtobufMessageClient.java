package com.freestyle.netty.easynetty.client.interfaces;

import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by rocklee on 2022/1/24 11:32
 */
public interface IProtobufMessageClient<T> extends IGeneralClient {
  void run(boolean waitForDisconnect, SimpleChannelInboundHandler<T> handler) throws InterruptedException;
}
