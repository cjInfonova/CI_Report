package com.infonova.jenkins;

//import com.bearingpoint.jbpm.TestExecutionContext;
import com.infonova.jenkins.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominic.gross on 21.07.2015.
 */

public class ReportMojoUTest extends EasyMockSupport{

    // @Parameter(defaultValue = "christian.jahrbacher")
    private final static String username = "christian.jahrbacher";
    // @Parameter(defaultValue = "Cj170615!")
    private final static String password = "Cj170615!";
    // @Parameter (defaultValue = "https://ci.infonova.at")
    public final static String JENKINS_URL = "https://ci.infonova.at";
    // @Parameter
    public final static String jobname = "A1OpenNet";
    // @Parameter(defaultValue = "dd.MM.yyyy")
    private final static SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");

    @Test
    public void execute()throws MojoExecutionException, MojoFailureException {
        List<JenkinsSystem> jenkinsSystemList = new ArrayList<JenkinsSystem>();
        Usersettings us = new Usersettings(username, password);
        JenkinsAccess jenkinsAccess = new JenkinsAccess(JENKINS_URL, us.getUsername(), us.getPassword());
        JobBuilder jobBuilder = new JobBuilder(jenkinsAccess, JENKINS_URL+"/job/"+jobname+"/job/", dateformat);
        HTMLGenerator htmlgen = new HTMLGenerator();
        DataAccessLayer dal = new DataAccessLayer(jenkinsAccess, JENKINS_URL, jobname, dateformat, jobBuilder, htmlgen);
        ReportMojo repo = new ReportMojo();

        replayAll();
        repo.execute();
        verifyAll();

    }

}
