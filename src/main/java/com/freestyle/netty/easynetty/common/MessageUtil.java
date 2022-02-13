package com.freestyle.netty.easynetty.common;

import com.freestyle.protobuf.proto.MessageOuterClass;
import com.google.protobuf.ByteString;

/**
 * Created by rocklee on 2022/1/21 15:22
 */
public class MessageUtil {
  public static MessageOuterClass.Message packMessage(Object object, String uuid, long id){
    MessageOuterClass.MessageProperties properties= MessageOuterClass.MessageProperties.newBuilder()
            .setId(id).setUuid(uuid).setSClass(object.getClass().getName()).build();
    MessageOuterClass.Message msg=MessageOuterClass.Message.newBuilder()
            .setProperties(properties).setData(ByteString.copyFrom(Utils.toJsonBytes(object))).build();
    return msg;
  }
  /**
   * 根据message属性里面的sClass还原到java 实体
   * @param message
   * @return
   * @throws ClassNotFoundException
   */
  public static Object dePackFromMessage(MessageOuterClass.Message message) throws ClassNotFoundException {
    MessageOuterClass.MessageProperties messageProperties=message.getProperties();
    Class tClass=Class.forName(messageProperties.getSClass());
    Object ins= Utils.fromJsonBytes(message.getData().toByteArray(),tClass);
    return ins;
  }
}
