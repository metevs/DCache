package com.metevs.dcache.facade.util;

import com.metevs.dcache.facade.domain.SortOrder;
import lombok.Data;

import java.io.Serializable;

@Data
public class FindSort implements Serializable {
    private SortOrder sortOrder = SortOrder.ASC;
    private String sortCell;
}
