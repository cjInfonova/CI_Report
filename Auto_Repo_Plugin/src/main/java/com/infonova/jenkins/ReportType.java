package com.infonova.jenkins;

import org.apache.maven.plugins.annotations.Mojo;
import org.json.JSONObject;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
@Mojo(name = "report_reporttype")
public class ReportType {

    private String reportName;
    private String result;
    private int failCount;
    private int totalCount;

    public ReportType(String name, String result, int failed, int total) {
        reportName = name;
        this.result = result;
        failCount = failed;
        totalCount = total;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getReportName() {
        return reportName;
    }

    public String getResult() {
        return result;
    }

    public int getFailCount() {
        return failCount;
    }

    @Override
    public String toString() {
        return String.format("%-45s %-10s %s", reportName, result, failCount > 0 ? (failCount + " NOK") : "OK");
    }
}
