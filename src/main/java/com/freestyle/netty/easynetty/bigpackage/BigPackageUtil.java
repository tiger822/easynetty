package com.freestyle.netty.easynetty.bigpackage;

import com.freestyle.netty.easynetty.common.ArrayUtil;
import com.freestyle.netty.easynetty.dto.BigPackageProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Supplier;

/**
 * Created by rocklee on 2022/2/17 10:23
 */
public class BigPackageUtil {
  private Channel channel;
  public BigPackageUtil(Channel channel) {
    this.channel=channel;
  }
  public void sendData(String id, Date timestamp, Supplier<byte[]> getData, long size,  boolean ... flush) {
    BigPackageProperties properties=new BigPackageProperties(id,size,timestamp);
    channel.write(properties);//先写入描述
    int len;
    long sended=0;
    for (;;){
      if (sended==size)break;
      byte[] data=getData.get();
      len=data.length;
      final ByteBuf byteBuf=Unpooled.copiedBuffer(data);
      channel.write(byteBuf).addListener(future -> { //ByteBuf 成功write之后，它会自行release
        if (!future.isSuccess()){
          byteBuf.retain(); //如果要重新使用，要retain增加引用计数器
          channel.write(byteBuf);
          future.cause().printStackTrace();
        }
      });
      sended+=len;
    }
    if (flush.length==0||!flush[0]) {
      channel.flush();
    }
  }
  public void sendData(String id, Date timestamp, InputStream is, long size, int frameLimit,boolean ... flush) throws IOException {
    BigPackageProperties properties=new BigPackageProperties(id,size,timestamp);
    channel.write(properties);//先写入描述
    byte[] buffer=new byte[frameLimit];
    for (;;){
      int readed=is.read(buffer,0,buffer.length);
      if (readed<=0)break;
      byte[] toSendBytes=readed==frameLimit?buffer: Arrays.copyOfRange(buffer,0,readed);
      final ByteBuf byteBuf=Unpooled.copiedBuffer(toSendBytes);
      channel.write(byteBuf).addListener(future -> { //ByteBuf 成功write之后，它会自行release
        if (!future.isSuccess()){
          byteBuf.retain(); //如果要重新使用，要retain增加引用计数器
          channel.write(byteBuf);
          future.cause().printStackTrace();
        }
      });
    }
    if (flush.length==0||!flush[0]) {
      channel.flush();
    }
  }
  public void sendData(String id, Date timestamp, byte[] bytes, long size, int frameLimit,boolean ... flush) {
    BigPackageProperties properties=new BigPackageProperties(id,size,timestamp);
    channel.write(properties);//先写入描述
    ArrayUtil.subList(bytes,frameLimit,sub->{
      final ByteBuf byteBuf=Unpooled.copiedBuffer(sub);
      channel.write(byteBuf).addListener(future -> { //ByteBuf 成功write之后，它会自行release
        if (!future.isSuccess()){
          byteBuf.retain(); //如果要重新使用，要retain增加引用计数器
          channel.write(byteBuf);
          future.cause().printStackTrace();
        }
      });
    });
    if (flush.length==0||!flush[0]) {
      channel.flush();
    }
  }
}
