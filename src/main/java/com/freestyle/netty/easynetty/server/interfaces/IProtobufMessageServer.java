package com.freestyle.netty.easynetty.server.interfaces;

import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Supplier;

/**
 * Created by rocklee on 2022/1/24 11:35
 */
public interface IProtobufMessageServer<T> extends IGeneralServer {
  void run(Supplier<SimpleChannelInboundHandler<T>> supplierHandler) throws InterruptedException;
}
