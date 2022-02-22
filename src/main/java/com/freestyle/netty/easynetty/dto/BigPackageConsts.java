package com.freestyle.netty.easynetty.dto;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rocklee on 2022/2/16 21:50
 */
public class BigPackageConsts {
  public static final Map<Integer,Class<?>> protocols=new HashMap<Integer, Class<?>>(){{
    put(10000, BigPackageProperties.class);
  }};
}
