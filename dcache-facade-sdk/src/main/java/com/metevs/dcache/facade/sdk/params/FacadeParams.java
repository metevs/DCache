package com.metevs.dcache.facade.sdk.params;

/**
 * file FacadeParams.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/8
 */
public class FacadeParams {

    /**
     * 当前应用名
     */
    private String applicationName;

    /**
     * zk主机地址
     */
    private String zkHost;

    private String protocol = "zookeeper";

    private String facadeVersion = "1.0.0";

    private Integer timeout = 10000;

    public FacadeParams() {
    }

    public FacadeParams(String applicationName, String zkHost, String protocol, String facadeVersion, Integer timeout) {
        this.applicationName = applicationName;
        this.zkHost = zkHost;
        this.protocol = protocol;
        this.facadeVersion = facadeVersion;
        this.timeout = timeout;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getZkHost() {
        return zkHost;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getFacadeVersion() {
        return facadeVersion;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public FacadeParams setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public FacadeParams setZkHost(String zkHost) {
        this.zkHost = zkHost;
        return this;
    }

    public FacadeParams setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public FacadeParams setFacadeVersion(String facadeVersion) {
        this.facadeVersion = facadeVersion;
        return this;
    }

    public FacadeParams setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }
}
