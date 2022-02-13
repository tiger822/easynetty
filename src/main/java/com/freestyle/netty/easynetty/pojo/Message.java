package com.freestyle.netty.easynetty.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by rocklee on 2022/1/20 12:17
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message<T> implements Serializable {
  private MessageProperties properties;
  private T data;

}
