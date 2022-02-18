package com.freestyle.netty.easynetty.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.freestyle.netty.easynetty.common.Utils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rocklee on 2022/2/16 21:38
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BigPackageProperties implements Serializable {
  private static transient TypeReference<BigPackageProperties> typeReference=new TypeReference<BigPackageProperties>() {
  };
  private String id; //数据的名称
  private Long total; //数据的总长度
  private long rt; //已收包长度
  private Date timestamp;
  @JsonIgnore
  private transient ByteBuf byteBuf;
  @JsonIgnore
  private transient ByteBuf tmpBuf;
  public byte[] toBytes(){
    return Utils.toJsonBytes(this);
  }
  public BigPackageProperties fromBytes(byte[] bytes){
    return Utils.fromJsonBytes(bytes,typeReference);
  }

  public BigPackageProperties(String id, Long total, Date timestamp) {
    this.id = id;
    this.total = total;
    this.timestamp = timestamp;
  }
}
