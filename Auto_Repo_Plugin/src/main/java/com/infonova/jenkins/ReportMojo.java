package com.infonova.jenkins;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Created by christian.jahrbacher on 14.07.2015.
 */
@Mojo(name = "report")
public class ReportMojo extends AbstractMojo {

    @Parameter
    private Usersettings usersettings;
    @Parameter
    // (defaultValue = "https://ci.infonova.at")
    private String jenkins_Url;
    @Parameter
    // (defaultValue = "A1OpenNet")
    private String jobname;
    @Parameter
    private List<JenkinsSystem> jenkinsSystemList;
    @Parameter
    // (defaultValue = "dd.MM.yyyy")
    private String dateformat;

    public void execute() throws MojoExecutionException, MojoFailureException {

        /*
         * Hier fehlt ein wenig der Flow
         * 
         * für meine verständnis wäre es
         * - input validieren
         * - instanzieren
         * 
         * - laden aller benötigten daten
         * - erzeugen output
         * 
         * der flow sollte irgendwo klar ersichtlich sein.
         */
        JenkinsClient jenkinsClient = new JenkinsClient(jenkins_Url, usersettings.getUsername(),
            usersettings.getPassword(), jenkins_Url + "/job/" + jobname + "/job/");
        JobBuilder jobBuilder = new JobBuilder(jenkinsClient, new SimpleDateFormat(dateformat));
        HTMLGenerator htmlgen = new HTMLGenerator();
        DataAccessLayer dal = new DataAccessLayer(jenkinsClient, new SimpleDateFormat(dateformat), jobBuilder, htmlgen,
            jenkinsSystemList);
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
