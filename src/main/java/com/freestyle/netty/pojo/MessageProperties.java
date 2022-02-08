package com.freestyle.netty.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by rocklee on 2022/1/20 16:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageProperties implements Serializable {
  private String uuid;
  private long id;
  @JsonProperty("sClass")
  private String sClass;

}
