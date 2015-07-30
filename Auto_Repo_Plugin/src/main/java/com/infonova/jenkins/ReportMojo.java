package com.infonova.jenkins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Created by christian.jahrbacher on 14.07.2015.
 */
@Mojo(name = "report")
public class ReportMojo extends AbstractMojo {

    //@Parameter(defaultValue = "christian.jahrbacher")
    private String username = "christian.jahrbacher";
    //@Parameter(defaultValue = "Cj170615!")
    private String password = "Cj170615!";
    //@Parameter(defaultValue = "https://ci.infonova.at")
    private String standardUrl = "https://ci.infonova.at";

    public void execute() throws MojoExecutionException, MojoFailureException {
        Usersettings us = new Usersettings(username, password);
        JenkinsAccess jenkinsAccess = new JenkinsAccess(standardUrl, us.getUsername(), us.getPassword());
        HTML_Generator htmlgen = new HTML_Generator();
        DataAccessLayer dal = new DataAccessLayer(jenkinsAccess,htmlgen);
        dal.setupJobList();
        dal.prepareEverything(jenkinsAccess);
        dal.readErrors();
        //dal.showAllFails();
        dal.generateHTML();
        //dal.showReports();
    }

}
