package com.infonova.jenkins;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
public class DataAccessLayer implements UrlParameter {

    private static Logger log = Logger.getLogger(DataAccessLayer.class.getName());
    // TODO: Remove this in next US
    private final static String sonar = "https://grzisesonar1.infonova.at/drilldown/issues/100648?period=2";
    private final static String codecove = "https://grzisesonar1.infonova.at/dashboard/index/100648?did=1&period=2";

    private List<JenkinsSystem> jenkinsSystemList;
    private SimpleDateFormat dateformat;
    private List<String> jobList;
    private List<Job> jobClassList;
    private List<Failure> failList;
    private RemoteClient remoteClient;
    private HTMLGenerator htmlgen;
    private JobBuilder jobBuilder;
    private Sonar dash;
    private Sonar detail;
    private SonarConfiguration sqc;
    private RemoteClient remoteSonar;

    public DataAccessLayer(RemoteClient jenAcc, SimpleDateFormat sdf, JobBuilder jobBuilder, HTMLGenerator htmlgen,
                           List<JenkinsSystem> jenkinsSystemList, SonarConfiguration sonarConfiguration, RemoteClient remoteSonar) {

        remoteClient = jenAcc;
        dateformat = sdf;
        this.htmlgen = htmlgen;
        this.jobBuilder = jobBuilder;
        this.jenkinsSystemList = jenkinsSystemList;
        this.sqc = sonarConfiguration;
        this.remoteSonar = remoteSonar

    }

    public void startBuildingReport() throws IOException, JenkinsException {
        for (JenkinsSystem js : jenkinsSystemList) {
            js.setJobList(jobBuilder.prepareEverything(js.getJobNameList()));
        }
        for (JenkinsSystem js : jenkinsSystemList) {
            if (js.getJobList() != null) {
                js.setFailList(new FailureBuilder(remoteClient, js.getJobNameList()).readErrors());

            }
        }
        String url4Sonardash = sqc.getBasicUrl()+"/api/resources?resource="+sqc.getComponentRoot()+"&includetrends=true&includealerts=true&format=json&period="+sqc.getPeriod()+"&metrics=new_coverage,ncloc,coverage,lines,files,statements,directories,classes,functions,accessors,open_issues,sqale_index,new_technical_debt,blocker_violations,critical_violations,major_violations,minor_violations,new_violations,info_violations";
        String url4Sonardetail = sqc.getBasicUrl()+"/api/issues/search?componentRoots="+sqc.getComponentRoot()+"&format=json&period="+sqc.getPeriod()+"&createdAfter="+sqc.getCreatedAfter()+"&statuses=OPEN,REOPENED";
        dash.setSonar(remoteClient.getJsonNodeFromUrl(url4Sonardash));
        detail.setSonar(remoteClient.getJsonNodeFromUrl(url4Sonardetail));

        generateHTML();
    }

    public void generateHTML() throws IOException {
        BufferedWriter bwr = null;

        File f = new File("data.html");
        FileWriter fr = new FileWriter(f);
        bwr = new BufferedWriter(fr);

        htmlgen.staticPreCode(bwr);
        for (JenkinsSystem job : jenkinsSystemList) {
            htmlgen.buildTable(job.getJobList(), bwr, job.getSystemName(), job.getFailList(), job.getColor());
        }

        htmlgen.staticPostCode(bwr, sonar, codecove);
        for (JenkinsSystem job : jenkinsSystemList) {
            htmlgen.buildFailureTable(job.getJobList(), bwr, job.getSystemName(), job.getFailList(), job.getColor());
        }
        bwr.write("</html>");
        bwr.close();
    }

    private List<Job> initList(String... name) {
        List<Job> list = new ArrayList<Job>();
        for (Job r : jobClassList) {
            String[] split = r.getJobName().split("-");
            for (String s : name) {
                if (split[split.length - 1].equals(s)) {
                    list.add(r);
                }
            }
        }
        return list;
    }

}
