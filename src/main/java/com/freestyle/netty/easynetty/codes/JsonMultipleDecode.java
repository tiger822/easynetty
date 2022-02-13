package com.freestyle.netty.easynetty.codes;

import com.freestyle.netty.easynetty.common.Utils;

/**
 * Created by rocklee on 2022/2/8 20:41
 */
public class JsonMultipleDecode extends AbstractMultipleDecode {
  @Override
  public Object decodeObject(byte[] data, Class<?> tClass) {
    return Utils.fromJsonBytes(data,tClass);
  }
}
