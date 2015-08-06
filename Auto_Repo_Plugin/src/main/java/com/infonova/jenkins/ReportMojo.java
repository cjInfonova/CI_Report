package com.infonova.jenkins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by christian.jahrbacher on 14.07.2015.
 */
@Mojo(name = "report")
public class ReportMojo extends AbstractMojo {

    @Parameter
    private Usersettings usersettings;
    @Parameter //(defaultValue = "https://ci.infonova.at")
    private String jenkins_Url;
    @Parameter //(defaultValue = "A1OpenNet")
    private String jobname;
    @Parameter
    private List<JenkinsSystem> jenkinsSystemList;
    @Parameter//(defaultValue = "dd.MM.yyyy")
    private String dateformat;

    public void execute() throws MojoExecutionException, MojoFailureException {
        JenkinsAccess jenkinsAccess = new JenkinsAccess(jenkins_Url, usersettings.getUsername(), usersettings.getPassword());
        JobBuilder jobBuilder = new JobBuilder(jenkinsAccess, jenkins_Url +"/job/"+jobname+"/job/", new SimpleDateFormat(dateformat));
        HTMLGenerator htmlgen = new HTMLGenerator();
        DataAccessLayer dal = new DataAccessLayer(jenkinsAccess, jenkins_Url, jobname, new SimpleDateFormat(dateformat), jobBuilder, htmlgen,jenkinsSystemList);
        try {
            dal.startBuildingReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public void setJenkins_Url(String jenkins_Url) {
        this.jenkins_Url = jenkins_Url;
    }

    public void setJenkinsSystemList(List<JenkinsSystem> jenkinsSystemList) {
        this.jenkinsSystemList = jenkinsSystemList;
    }
    public void setDateformat(String dateformat) {
        this.dateformat = dateformat;
    }
}
