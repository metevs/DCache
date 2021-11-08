package com.metevs.dcache.facade.query;

import com.metevs.dcache.facade.util.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MustQueryBuilder<T> implements Serializable {
    String fieldName;
    QueryType.NullType nullType;
    T defaultValue;
}