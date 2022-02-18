package com.freestyle.netty.easynetty.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 数组工具类
 * Created by rocklee on 2021/3/11 10:25
 */
public class ArrayUtil {
  /**
   * 分隔数组 根据每段数量分段
   *
   * @param data  被分隔的数组
   * @param frameLimit 每段数量
   * @return
   */
  public static <T> List<List<T>> subListByCount(List<T> data, int frameLimit) {
    List<List<T>> result = new ArrayList<>();
    int size = data.size();// 数据长度

    if (size > 0 && frameLimit > 0) {

      int segments = size / frameLimit;// 商
      /**
       * 1.整除，  即分隔为segments     段
       * 2.除不尽，即分隔为segments + 1 段
       */
      segments = size % frameLimit == 0 ? segments : segments + 1; // 段数

      List<T> cutList;// 每段List

      for (int i = 0; i < segments; i++) {
        if (i == segments - 1) {
          cutList = data.subList(frameLimit * i, size);
        } else {
          cutList = data.subList(frameLimit * i, frameLimit * (i + 1));
        }
        result.add(cutList);
      }
    } else {
      result.add(data);
    }
    return result;
  }
  /**
   * 分隔数组 根据每段数量分段
   *
   * @param data  被分隔的数组
   * @param frameLimit 每段数量
   * @return
   */
  public   static <T> void   subListByCount(List<T> data, int frameLimit, Consumer<List<T>> output) {
    int size = data.size();// 数据长度

    if (size > 0 && frameLimit > 0) {

      int segments = size / frameLimit;// 商
      /**
       * 1.整除，  即分隔为segments     段
       * 2.除不尽，即分隔为segments + 1 段
       */
      segments = size % frameLimit == 0 ? segments : segments + 1; // 段数

      List<T> cutList;// 每段List

      for (int i = 0; i < segments; i++) {
        if (i == segments - 1) {
          cutList = data.subList(frameLimit * i, size);
        } else {
          cutList = data.subList(frameLimit * i, frameLimit * (i + 1));
        }
        output.accept(cutList);
      }
    } else {
      output.accept(data);
    }
  }
  /**
   * 分隔数组 根据每段数量分段
   *
   * @param data  被分隔的数组
   * @param frameLimit 每段数量
   * @return
   */
  public static <T> void subList(T[] data, int frameLimit, Consumer<T[]> output) {
    int size = data.length;// 数据长度

    if (size > 0 && frameLimit > 0) {

      int segments = size / frameLimit;// 商
      /**
       * 1.整除，  即分隔为segments     段
       * 2.除不尽，即分隔为segments + 1 段
       */
      segments = size % frameLimit == 0 ? segments : segments + 1; // 段数

      T[] cutList;// 每段List

      for (int i = 0; i < segments; i++) {
        if (i == segments - 1) {
          cutList =Arrays.copyOfRange(data,frameLimit * i, size);
        } else {
          cutList = Arrays.copyOfRange(data,frameLimit * i,frameLimit * (i + 1));
        }
        output.accept(cutList);
      }
    } else {
      output.accept(data);
    }
  }
  /**
   * 分隔数组 根据每段数量分段
   *
   * @param data  被分隔的数组
   * @param frameLimit 每段数量
   * @return
   */
  public static  void subList(byte[] data, int frameLimit, Consumer<byte[]> output) {
    int size = data.length;// 数据长度

    if (size > 0 && frameLimit > 0) {

      int segments = size / frameLimit;// 商
      /**
       * 1.整除，  即分隔为segments     段
       * 2.除不尽，即分隔为segments + 1 段
       */
      segments = size % frameLimit == 0 ? segments : segments + 1; // 段数

      byte[] cutList;// 每段List

      for (int i = 0; i < segments; i++) {
        if (i == segments - 1) {
          cutList =Arrays.copyOfRange(data,frameLimit * i, size);
        } else {
          cutList = Arrays.copyOfRange(data,frameLimit * i,frameLimit * (i + 1));
        }
        output.accept(cutList);
      }
    } else {
      output.accept(data);
    }
  }
  public static void main(String[] args){
    List<String> data= Arrays.asList("1234","2345","abc","def","fhis","253aas","slkda");
    System.out.println(data);
    ArrayUtil.subListByCount(data,3,(s)->{
      System.out.println(s);
    });
     /*List<List<String>>subItems= ArrayUtil.subListByCount(data,3);
     if (subItems!=null){
     }*/
  }
}
