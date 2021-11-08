package com.metevs.dcache.facade.query;

import com.metevs.dcache.facade.util.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class EqualQueryBuilder<T> implements Serializable {
    String fieldName;
    T t;
    List<T> ts;
    QueryType.EqualType equalType;
}
