package com.infonova.jenkins;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
@Mojo(name = "report_reporttype")
public class ReportType {

    private String reportName;
    private String result;
    private int failCount;
    private int totalCount;
    private String lastStableDate;

    public ReportType(String name, String result, int failed, int total, String lastStable) {
        reportName = name;
        this.result = result;
        failCount = failed;
        totalCount = total;
        this.lastStableDate = lastStable;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getReportName() {
        return reportName;
    }

    public String getResult() {
        return String.format("%s", failCount > 0 ? (failCount + " NOK") : "OK");
    }

    public int getFailCount() {
        return failCount;
    }

    public String getLastStableDate() {
        return lastStableDate;
    }

    @Override
    public String toString() {
        return String.format("%-45s %-10s %-10s %-12s", reportName, result,
            failCount > 0 ? (failCount + " NOK") : "OK", lastStableDate);
    }
}
