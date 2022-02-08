package com.freestyle.netty.server.intefaces;

import com.freestyle.netty.server.GeneralServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;

import java.util.function.Consumer;

/**
 * Created by rocklee on 2022/1/24 11:07
 */
public interface IGeneralServer {
  GeneralServer setLogLevel(LogLevel logLevel);

  ServerBootstrap getServerBootstrap();

  IGeneralServer run(ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException;

  IGeneralServer run(ChannelInitializer<SocketChannel> channelInitializer,boolean await) throws InterruptedException;

  IGeneralServer run(Consumer<SocketChannel> initChannel) throws InterruptedException;
  IGeneralServer run(Consumer<SocketChannel> initChannel,boolean await) throws InterruptedException;
  void await();
  void close();

}
