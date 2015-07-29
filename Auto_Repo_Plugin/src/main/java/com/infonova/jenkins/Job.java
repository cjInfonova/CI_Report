package com.infonova.jenkins;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
// @Mojo(name = "report_reporttype")
public class Job {

    private String jobName;
    private String result;
    private int failCount;
    private int totalCount;
    private String lastStableDate;

    public Job(){}

    public Job(String name, String result, int failed, int total, String lastStable) {
        jobName = name;
        this.result = result;
        failCount = failed;
        totalCount = total;
        this.lastStableDate = lastStable;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getLastStableDate() {
        return lastStableDate;
    }

    public void setLastStableDate(String lastStableDate) {
        this.lastStableDate = lastStableDate;
    }

    @Override
    public String toString() {
        return String.format("%-45s %-10s %-10s %-12s", jobName, result, failCount > 0 ? (failCount + " NOK") : "OK",
                lastStableDate);
    }

    public String getResult() {
        return String.format("%s",
                result.equals("SUCCESS") ? "OK" : result.equals("FAILURE") || result.equals("ABORTED") || result.equals("RUNNING") ? result : failCount
                        + " NOK");
    }
}
