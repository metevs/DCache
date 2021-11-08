package com.metevs.dcache.facade.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class MustQueryParam implements Serializable {
   private String filedName;
   private Integer mustOrNot; //1为空 2不为空
}
