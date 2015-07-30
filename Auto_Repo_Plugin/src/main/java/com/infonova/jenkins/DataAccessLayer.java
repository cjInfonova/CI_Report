package com.infonova.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
public class DataAccessLayer implements UrlParameters {

    private List<JenkinsSystem> jenkinsSystemList;
    private SimpleDateFormat dateformat;
    private String standardUrl;
    private List<String> jobList;
    private List<Job> jobClassList;
    private List<Failure> failList;
    private static Logger log = Logger.getLogger("MyLogger");
    private final static String sonar = "https://grzisesonar1.infonova.at/drilldown/issues/100648?period=2";
    private final static String codecove = "https://grzisesonar1.infonova.at/dashboard/index/100648?did=1&period=2";
    private final static String excelpath = "\\\\filesrv.techu.local\\public\\LOB\\CC_INFONOVA_SOFTWARE_ENGINEERING\\OpenNet_CI\\Failed_Tests.xlsx";
    private JenkinsAccess jenkinsAccess;
    private HTML_Generator htmlgen;

    public DataAccessLayer(JenkinsAccess jenAcc, String jenkinsUrl, String jobname, SimpleDateFormat sdf,
            HTML_Generator htmlgen) {
        jenkinsAccess = jenAcc;
        standardUrl = jenkinsUrl + "/job/" + jobname + "/job/";
        dateformat = sdf;
        this.htmlgen = htmlgen;
    }

    public void startBuildingReport() {
        setupJobList();
        prepareEverything();
        failList = new FailureBuilder(jenkinsAccess, jobList, standardUrl).readErrors();
        generateHTML();
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

    private void prepareEverything() {
        jobClassList = new ArrayList<Job>();
        try {
            for (String jobString : jobList) {
                try {
                    String url = standardUrl + jobString;
                    JsonNode jsNode = jenkinsAccess.getJsonNodeFromUrl(url + LAST_STATE + JSON_EXTENTION);
                    Job job = convertJsonNodeIntoJob(jsNode);
                    if (!job.getResult().equals("SUCCESS")) {
                        jsNode = jenkinsAccess.getJsonNodeFromUrl(url + STABLE_STATE + JSON_EXTENTION);
                        job.setLastStableDate(dateformat.format(getLastStableDateFromJsonNode(jsNode)));
                    }
                    job.setJobName(jobString);
                    jobClassList.add(job);
                } catch (JenkinsException jex) {
                    if (jex.getMessage().contains("Source not found")) {
                        log.info(jex.getMessage() + ": " + jobString);
                    } else {
                        throw jex;
                    }
                } catch (IOException exe) {
                    log.info("An unexpected error has occurred");
                    exe.printStackTrace();
                    exe.printStackTrace();
                } catch (Throwable exe) {
                    log.info(exe.getMessage());
                    exe.printStackTrace();
                }
            }
            log.info("Reports konnten geladen werden: " + jobClassList.size() + "/" + jobList.size());
        } catch (JenkinsException ulexe) {
            log.info(ulexe.getMessage());
        }
    }

    private Job convertJsonNodeIntoJob(JsonNode jsNode) {
        Job job = new Job();
        if (jsNode.get("building").asBoolean()) {
            job = new Job("-", "RUNNING", 0, 0, "-");
            return job;
        }
        job.setResult(jsNode.get("result").asText());
        job.setLastStableDate(dateformat.format(getLastStableDateFromJsonNode(jsNode)));
        if (jsNode.get("actions").isArray()) {
            for (JsonNode jn : jsNode.get("actions")) {
                if (!jn.isArray() && jn.has("failCount")) {
                    job.setFailCount(jn.get("failCount").asInt());
                    job.setTotalCount(jn.get("totalCount").asInt());
                }
            }
        }
        return job;
    }

    private Date getLastStableDateFromJsonNode(JsonNode jsNode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(jsNode.get("id").asText());
        } catch (ParseException pex) {
            log.info("Can't parse date: " + jsNode.get("id").asText());
        }
        return date;
    }
}
