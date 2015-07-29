package com.infonova.jenkins;

import java.awt.*;
import java.util.List;

/**
 * Created by christian.jahrbacher on 28.07.2015.
 */
public class JenkinsSystem {

    private String systemName;
    private Color[] colors = new Color[3];
    private List<String> jobNameList;
    private List<Job> jobList;

    public JenkinsSystem(String name, Color[] cols, List<Job> jobs) {
        systemName = name;
        colors = cols;
        jobList = jobs;
    }

    public String getSystemName() {
        return systemName;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public List<String> getJobNameList() {
        return jobNameList;
    }
}
