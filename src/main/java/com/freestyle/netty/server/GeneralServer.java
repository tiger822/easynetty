package com.freestyle.netty.server;

import com.freestyle.netty.server.intefaces.IGeneralServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.function.Consumer;

/**
 * Created by rocklee on 2022/1/20 16:08
 */
public class GeneralServer implements AutoCloseable, com.freestyle.netty.server.intefaces.IGeneralServer {
  private final EventLoopGroup bossGroup ;
  private final EventLoopGroup workGroup ;
  protected final ServerBootstrap serverBootstrap;
  protected final int port;
  private ChannelFuture channelFuture;
  private LogLevel logLevel=LogLevel.INFO;
  @Override
  public GeneralServer setLogLevel(LogLevel logLevel) {
    this.logLevel = logLevel;
    serverBootstrap.handler(new LoggingHandler(logLevel));
    return this;
  }
  @Override
  public ServerBootstrap getServerBootstrap() {
    return serverBootstrap;
  }

  @Override
  public IGeneralServer run(ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException {
    return run(channelInitializer,true);
  }

  public GeneralServer(int port) {
    this(new NioEventLoopGroup(),new NioEventLoopGroup(),port,LogLevel.INFO);
  }
  @Override
  public IGeneralServer run(ChannelInitializer<SocketChannel> channelInitializer,boolean  await) throws InterruptedException {
    serverBootstrap.childHandler(channelInitializer);
    channelFuture= serverBootstrap.bind(port).sync();
    if (await) {
      channelFuture.channel().closeFuture().sync();
    }
    return this;
  }

  @Override
  public IGeneralServer run(Consumer<SocketChannel> initChannel) throws InterruptedException {
    return run(initChannel,true);
  }

  @Override
  public IGeneralServer run(Consumer<SocketChannel> initChannel,boolean  await) throws InterruptedException {
    serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                initChannel.accept(ch);
              }
            });
    channelFuture = serverBootstrap.bind(port).sync();
    if (await) {
      channelFuture.channel().closeFuture().sync();
    }
    return this;
  }

  @Override
  public void await() {
    try {
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public GeneralServer(EventLoopGroup bossGroup, EventLoopGroup workGroup,int port,LogLevel  logLevel) {
    this.port=port;
    this.bossGroup = bossGroup;
    this.workGroup = workGroup;
    serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
    .childOption(ChannelOption.TCP_NODELAY,true);
    this.setLogLevel(logLevel);
  }
  @Override
  public void close()  {
    bossGroup.shutdownGracefully();
    workGroup.shutdownGracefully();
  }

  }
