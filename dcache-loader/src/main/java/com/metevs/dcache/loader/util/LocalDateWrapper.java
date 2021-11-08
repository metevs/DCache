package com.metevs.dcache.loader.util;

import com.metevs.dcache.facade.util.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateWrapper {

    private LocalDateTime localDateTime;

    private QueryType.CompareType compareType;

    private String cellName;
}
