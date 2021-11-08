package com.metevs.dcache.facade.domain;

import com.metevs.dcache.facade.query.QueryEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class CacheMsg implements Serializable {
    private QueryEntity type; //数据类型
    private String value;     //JSON string
    private Long tag;         //用于hash分区（一般主键）
    private String label;     //
    private Integer subType;  //merchantId

    public CacheMsg() {
    }

    public CacheMsg(QueryEntity type, String value, Long tag, String label) {
        this.type = type;
        this.value = value;
        this.tag = tag;
        this.label = label;
    }

    public CacheMsg(QueryEntity type, String value, Long tag, String label, Integer subType) {
        this.type = type;
        this.value = value;
        this.tag = tag;
        this.label = label;
        this.subType = subType;
    }

    public QueryEntity getType() {
        return this.type;
    }

    public void setType(QueryEntity type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTag() {
        return this.tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getSubType() {
        return this.subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public String toString() {
        return "TVMsg{type=" + this.type + ", value='" + this.value + '\'' + ", tag=" + this.tag + ", label='" + this.label + '\'' + ", subType=" + this.subType + '}';
    }

}
