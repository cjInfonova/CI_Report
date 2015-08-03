package com.infonova.jenkins;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
public class DataAccessLayer implements IUrlParameters {

    private List<JenkinsSystem> jenkinsSystemList;
    private SimpleDateFormat dateformat;
    private String standardUrl;
    private List<String> jobList;
    private List<Job> jobClassList;
    private List<Failure> failList;
    private static Logger log = Logger.getLogger("MyLogger");
    private final static String sonar = "https://grzisesonar1.infonova.at/drilldown/issues/100648?period=2";
    private final static String codecove = "https://grzisesonar1.infonova.at/dashboard/index/100648?did=1&period=2";
    private JenkinsAccess jenkinsAccess;
    private HTMLGenerator htmlgen;
    private JobBuilder jobBuilder;

    public DataAccessLayer(JenkinsAccess jenAcc, String jenkinsUrl, String jobname, SimpleDateFormat sdf,
            JobBuilder jobBuilder, HTMLGenerator htmlgen) {
        jenkinsAccess = jenAcc;
        standardUrl = jenkinsUrl + "/job/" + jobname + "/job/";
        dateformat = sdf;
        this.htmlgen = htmlgen;
        this.jobBuilder = jobBuilder;
    }

    public void startBuildingReport() {
        setupJobList();
        jobClassList = jobBuilder.prepareEverything(jobList);
        if(jobClassList!=null){
            failList = new FailureBuilder(jenkinsAccess, jobList, standardUrl).readErrors();
            generateHTML();
        }
    }

    public void generateHTML() {
        BufferedWriter bwr = null;
        try {
            List<Job> uat4 = initList("UAT4");
            List<Job> trunk = initList("trunk", "trunk12c");
            List<Job> rc = initList("rc");
            List<Job> bf = initList("bf");
            List<Job> rc2 = initList("rc2");

            File f = new File("data.html");
            FileWriter fr = new FileWriter(f);
            bwr = new BufferedWriter(fr);

            htmlgen.staticPreCode(bwr);

            htmlgen.buildTable(trunk, bwr, "TRUNK", failList);
            htmlgen.buildTable(rc, bwr, "RC", failList);
            htmlgen.buildTable(rc2, bwr, "RC2", failList);
            htmlgen.buildTable(bf, bwr, "BF", failList);
            htmlgen.buildTable(uat4, bwr, "UAT4", failList);

            htmlgen.staticPostCode(bwr, jobClassList, failList, sonar, codecove);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bwr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void setupJobList() {
        jobList = new ArrayList<String>();
        // Trunk12c
        jobList.add("A1ON-java-build-trunk");
        jobList.add("A1ON-jtf-livetests-trunk12c");
        jobList.add("A1ON-jtf-db-infrastructure-trunk12c");
        jobList.add("A1ON-jtf-db-regressiontests-trunk12c");
        jobList.add("A1ON-jtf-db-guidelines-trunk12c");
        jobList.add("A1ON-jtf-project-tests-trunk12c");
        jobList.add("A1ON-jtf-regressiontests-trunk12c");
        // RC2
        jobList.add("A1ON-java-build-rc2");
        jobList.add("A1ON-jtf-db-guidelines-rc2");
        jobList.add("A1ON-jtf-db-regressiontests-rc2");
        jobList.add("A1ON-jtf-regressiontests-rc2");
        jobList.add("A1ON-jtf-livetests-rc2");
        // RC
        jobList.add("A1ON-java-build-rc");
        jobList.add("A1ON-jtf-db-guidelines-rc");
        jobList.add("A1ON-jtf-regressiontests-rc");
        jobList.add("A1ON-jtf-smoketests-rc");
        // BF
        jobList.add("A1ON-java-build-bf");
        jobList.add("A1ON-jtf-db-guidelines-bf");
        jobList.add("A1ON-jtf-regressiontests-bf");
        jobList.add("A1ON-jtf-db-regressiontests-bf");
        jobList.add("A1ON-jtf-smoketests-bf");
        // UAT4
        jobList.add("A1ON-jtf-db-infrastructure-UAT4");
        jobList.add("A1ON-jtf-db-regressiontests-UAT4");
        jobList.add("A1ON-jtf-regressiontests-UAT4");
        jobList.add("A1ON-jtf-smoketests-UAT4");
    }

    public void showReports() {
        System.out.printf("%-45s %-10s %-10s %-15s\n", "Step", "Result", "Ergebnis", "LastStableDate");
        for (Job rt : jobClassList) {
            System.out.println(rt.toString());
        }
    }

    public void showAllFails() {
        for (Failure f : failList) {
            if (!f.getFailure().equals("NoFailure")) {
                System.out.println(f.toString());
            }
        }
    }
}
