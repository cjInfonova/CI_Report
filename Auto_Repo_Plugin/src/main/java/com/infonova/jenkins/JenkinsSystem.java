package com.infonova.jenkins;

import java.util.List;

/**
 * Created by christian.jahrbacher on 28.07.2015.
 */
public class JenkinsSystem {

    private String systemName;
    private String color;
    private List<String> jobNameList;
    private List<Job> jobList;
    private List<Failure> failList;

    public JenkinsSystem(){}

    public JenkinsSystem(String name, String cols, List<String> jobs) {
        systemName = name;
        color = cols;
        jobNameList = jobs;
    }

    @Override
    public String toString(){
        String returnString = systemName + " - Jobs:\n";
        for(String s : jobNameList){
            returnString += s;
        }
        return returnString;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String colors) {
        this.color = colors;
    }

    public List<String> getJobNameList() {
        return jobNameList;
    }

    public void setJobNameList(List<String> jobNameList) {
        this.jobNameList = jobNameList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public void setFailList(List<Failure> failList) {
        this.failList = failList;
    }

    public List<Failure> getFailList() {
        return failList;
    }
}
