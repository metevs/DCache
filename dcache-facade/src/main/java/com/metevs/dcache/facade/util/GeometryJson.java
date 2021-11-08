package com.metevs.dcache.facade.util;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeometryJson<T> implements Serializable {
    private static final long serialVersionUID = -1L;
    private String type;
    private List<T> coordinates;

    public GeometryJson() {
    }

    public GeometryJson(String type, List<T> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<T> getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(List<T> coordinates) {
        this.coordinates = coordinates;
    }
}
