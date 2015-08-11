package com.infonova.jenkins;

/**
 * Created by christian.jahrbacher on 11.08.2015.
 */
public class SonarConfiguration {
    private String basicUrl;
    private String componentRoot;
    private String period;
    private String createdAfter;

    public SonarConfiguration(){}

    public SonarConfiguration(String basicUrl, String componentRoot, String period, String createdAfter){
        this.basicUrl = basicUrl;
        this.componentRoot = componentRoot;
        this.period = period;
        this.createdAfter = createdAfter;
    }

    public String getComponentRoot() {
        return componentRoot;
    }

    public void setComponentRoot(String componentRoot) {
        this.componentRoot = componentRoot;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(String createdAfter) {
        this.createdAfter = createdAfter;
    }

    public String getBasicUrl() {
        return basicUrl;
    }

    public void setBasicUrl(String basicUrl) {
        this.basicUrl = basicUrl;
    }
}
