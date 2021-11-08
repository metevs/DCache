package com.metevs.dcache.facade.query;

import com.metevs.dcache.facade.util.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class RangeQueryBuilder<T> implements Serializable {
    String fieldName;
    QueryType.CompareType compareType;
    T t;
    List<T> ts;
}
