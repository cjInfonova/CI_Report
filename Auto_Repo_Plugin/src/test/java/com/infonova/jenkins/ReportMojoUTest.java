package com.infonova.jenkins;

//import com.bearingpoint.jbpm.TestExecutionContext;
import com.infonova.jenkins.*;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dominic.gross on 21.07.2015.
 */

public class ReportMojoUTest {

    // @Parameter(defaultValue = "christian.jahrbacher")
    private static String username = "christian.jahrbacher";
    // @Parameter(defaultValue = "Cj170615!")
    private static String password = "Cj170615!";
    // @Parameter (defaultValue = "https://ci.infonova.at")
    public static String JENKINS_URL = "https://ci.infonova.at";
    // @Parameter
    public static String jobname = "A1OpenNet";
    // @Parameter
    private static List<JenkinsSystem> jenkinsSystemList;
    // @Parameter(defaultValue = "dd.MM.yyyy")
    private static SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");
    @Test
    public void execute() throws IOException {
        Usersettings us = new Usersettings(username, password);
        JenkinsAccess jenkinsAccess = new JenkinsAccess(JENKINS_URL, us.getUsername(), us.getPassword());
        JobBuilder jobBuilder = new JobBuilder(jenkinsAccess, JENKINS_URL+"/job/"+jobname+"/job/", dateformat);
        HTMLGenerator htmlgen = new HTMLGenerator();
        DataAccessLayer dal = new DataAccessLayer(jenkinsAccess, JENKINS_URL, jobname, dateformat, jobBuilder, htmlgen);
        dal.startBuildingReport();

    }

}
