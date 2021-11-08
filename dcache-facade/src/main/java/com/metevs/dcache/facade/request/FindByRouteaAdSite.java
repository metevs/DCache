package com.metevs.dcache.facade.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class FindByRouteaAdSite implements Serializable {
    private Long merchantId;
    private Long siteId;
    private Long routeId;
}
