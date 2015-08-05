package com.infonova.jenkins;

import java.awt.*;
import java.util.List;

/**
 * Created by christian.jahrbacher on 28.07.2015.
 */
public class JenkinsSystem {

    private String systemName;
    private String[] colors;
    private List<String> jobNameList;
    private List<Job> jobList;

    public JenkinsSystem(){}

    public JenkinsSystem(String name, String[] cols, List<Job> jobs) {
        systemName = name;
        colors = cols;
        jobList = jobs;
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

    public String[] getColors() {
        return colors;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
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
}
