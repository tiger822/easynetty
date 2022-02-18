package com.freestyle.netty.easynetty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by rocklee on 2022/2/16 21:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BigPackage implements Serializable {
  private BigPackageProperties properties;
  private byte[] data;
}
