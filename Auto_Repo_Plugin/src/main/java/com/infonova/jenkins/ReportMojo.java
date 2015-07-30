package com.infonova.jenkins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by christian.jahrbacher on 14.07.2015.
 */
@Mojo(name = "report")
public class ReportMojo extends AbstractMojo {

    //@Parameter(defaultValue = "christian.jahrbacher")
    private String username = "christian.jahrbacher";
    //@Parameter(defaultValue = "Cj170615!")
    private String password = "Cj170615!";
    // @Parameter (defaultValue = "https://ci.infonova.at")
    public String JENKINS_URL = "https://ci.infonova.at";
    // @Parameter
    public String jobname = "A1OpenNet";
    // @Parameter
    private List<JenkinsSystem> jenkinsSystemList;
    // @Parameter(defaultValue = "dd.MM.yyyy")
    private SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");


    public void execute() throws MojoExecutionException, MojoFailureException {
        Usersettings us = new Usersettings(username, password);
        JenkinsAccess jenkinsAccess = new JenkinsAccess(JENKINS_URL, us.getUsername(), us.getPassword());

        DataAccessLayer dal = new DataAccessLayer(jenkinsAccess, JENKINS_URL, jobname, dateformat);
        dal.startBuildingReport();
    }

}
