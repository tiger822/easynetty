package com.freestyle.netty.easynetty.client.interfaces;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;

import java.util.function.Consumer;

/**
 * Created by rocklee on 2022/1/24 11:14
 */
public interface IGeneralClient {
  IGeneralClient setLogLevel(LogLevel logLevel);

  Bootstrap getBootstrap();

  Channel getChannel();

  void run(boolean waitForDisconnect, ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException;

  void run(boolean waitForDisconnect, Consumer<SocketChannel> initChannel) throws InterruptedException;
  void connect() throws InterruptedException;
  void disconnect() throws InterruptedException;
  boolean isConnected();
  void close() throws InterruptedException;

}
