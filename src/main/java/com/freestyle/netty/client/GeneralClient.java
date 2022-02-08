package com.freestyle.netty.client;

import com.freestyle.netty.client.interfaces.IGeneralClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;

/**
 * Created by rocklee on 2022/1/20 16:08
 */
public class GeneralClient implements AutoCloseable, com.freestyle.netty.client.interfaces.IGeneralClient {
  private final EventLoopGroup bossGroup ;
  protected final Bootstrap bootstrap;
  protected final int port;
  protected final String host;
  private LogLevel logLevel=LogLevel.INFO;
  protected ChannelFuture channelFuture=null;

  @Override
  public IGeneralClient setLogLevel(LogLevel logLevel) {
    this.logLevel = logLevel;
    bootstrap.handler(new LoggingHandler(logLevel));
    return this;
  }

  @Override
  public Bootstrap getBootstrap() {
    return bootstrap;
  }

  public GeneralClient(String host,int port) {
    this(new NioEventLoopGroup(),host,port,LogLevel.INFO);
  }

  @Override
  public Channel getChannel(){
    return channelFuture==null?null:channelFuture.channel();
  }
  private enum RunType{runNone,runAsChannelInitializer,runAsConsumer};
  private RunType runType=RunType.runNone;
  private ChannelInitializer<SocketChannel> lastChannelInitializer=null;
  private Consumer<SocketChannel> lastConsumer=null;
  @Override
  public void run(boolean waitForDisconnect, ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException {
    runType=RunType.runAsChannelInitializer;
    lastChannelInitializer=channelInitializer;
    bootstrap.handler(channelInitializer);
    ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
    if (waitForDisconnect) {
      channelFuture.channel().closeFuture().sync();
    }
  }

  /***c
   * 同步监听！
   */
  @Override
  public void run(boolean waitForDisconnect, Consumer<SocketChannel> initChannel) throws InterruptedException {
    runType=RunType.runAsConsumer;
    lastConsumer=initChannel;
    bootstrap.handler(new ChannelInitializer<SocketChannel>(){
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                initChannel.accept(ch);
              }
            });
    channelFuture = bootstrap.connect(host,port).sync();
    if (waitForDisconnect) {
      channelFuture.channel().closeFuture().sync();
    }
  }

  @Override
  public void connect() throws InterruptedException {
    if (runType==RunType.runNone){
      throw new IllegalStateException("execute run method first.");
    }
    if (runType==RunType.runAsChannelInitializer){
      bootstrap.handler(lastChannelInitializer);
    }
    else{
      bootstrap.handler(new ChannelInitializer<SocketChannel>(){
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          lastConsumer.accept(ch);
        }
      });
    }
    channelFuture = bootstrap.connect(host,port).sync();
  }

  @Override
  public void disconnect() throws InterruptedException {
      getChannel().disconnect().sync();
  }

  @Override
  public boolean isConnected() {
    return getChannel()!=null&&getChannel().isActive();
  }

  public GeneralClient(EventLoopGroup bossGroup,String host, int port,LogLevel logLevel) {
    this.port=port;
    this.host=host;
    this.bossGroup = bossGroup;
    bootstrap = new Bootstrap();
    bootstrap.group(bossGroup).channel(NioSocketChannel.class);
    this.setLogLevel(logLevel);
  }
  @Override
  public void close() throws java.lang.InterruptedException {
    if (getChannel()!=null){
      getChannel().close().sync();
    }
    bossGroup.shutdownGracefully();
  }
}
