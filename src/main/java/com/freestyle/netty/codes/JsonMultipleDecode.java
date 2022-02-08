package com.freestyle.netty.codes;

import com.freestyle.netty.common.Utils;

/**
 * Created by rocklee on 2022/2/8 20:41
 */
public class JsonMultipleDecode extends AbstractMultipleDecode {
  @Override
  public Object decodeObject(byte[] data, Class<?> tClass) {
    return Utils.fromJsonBytes(data,tClass);
  }
}
