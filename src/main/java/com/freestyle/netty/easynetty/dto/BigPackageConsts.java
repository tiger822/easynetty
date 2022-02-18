package com.freestyle.netty.easynetty.dto;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rocklee on 2022/2/16 21:50
 */
public class BigPackageConsts {
  public static final Map<byte[],Class<?>> protocols=new HashMap<byte[], Class<?>>(){{
    put(ByteBuffer.allocate(4).putInt(10000).array(), BigPackageProperties.class);
  }};
}
