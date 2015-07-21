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

    public void execute() throws MojoExecutionException, MojoFailureException {
        DataAccessLayer dal = new DataAccessLayer();
        dal.setupJobList();
        dal.getAllJsonsFromJenkins();
        dal.generateHTML();
    }

}
