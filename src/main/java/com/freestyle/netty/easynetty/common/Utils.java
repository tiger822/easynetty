package com.freestyle.netty.easynetty.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rocklee on 2022/1/20 14:07
 */
@Slf4j
public class Utils {
  private static final ConcurrentHashMap<Class<?>, ObjectMapper> mappers=new ConcurrentHashMap<>();
  public static ObjectMapper getMapper(Class<?> tClass){
     if (mappers.containsKey(tClass)){
       return mappers.get(tClass);
     }
     ObjectMapper mapper=new ObjectMapper();
     mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));//解决时区差8小时问题
     //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.constructType(tClass);
    mappers.put(tClass,mapper);
      return mapper;
  }
  public static byte[] toJsonBytes(Object o){
    try {
      return getMapper(o.getClass()).writeValueAsBytes(o);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
  public static String toJsonString(Object o){
    try {
      return getMapper(o.getClass()).writeValueAsString(o);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
  public static <T> T fromJson(String jsonStr,Class<T> tClass) {
    try {
      return getMapper(tClass).readValue(jsonStr,tClass);
    } catch (IOException e) {
      //log.error("fromJsonBytes",e);
      e.printStackTrace();
      return null;
    }
  }
  public static <T> T fromJsonBytes(byte[] bytes,Class<T> tClass) {
    try {
      return getMapper(tClass).readValue(bytes,tClass);
    } catch (IOException e) {
        //log.error("fromJsonBytes",e);
        e.printStackTrace();
        return null;
    }
  }
}
