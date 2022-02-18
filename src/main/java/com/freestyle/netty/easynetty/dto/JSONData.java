package com.freestyle.netty.easynetty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by rocklee on 2022/2/15 10:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JSONData<T> {
  private long id;
  private int errCode;
  private String message;
  private T result;
  public static <V> JSONData<V> fromResult(long id,V val){
    JSONData<V> ret = new JSONData<>();
    ret.result = val;
    ret.id=id;
    return ret;
  }
  public static JSONData fromErr(long id,int errCode,String message){
    JSONData ret=new JSONData(id,errCode,message,null);
    return ret;
  }

}
