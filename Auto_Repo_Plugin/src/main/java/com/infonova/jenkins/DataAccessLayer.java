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
public class DataAccessLayer implements UrlParameters{

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
    private int starthoch = 0;

    public DataAccessLayer(JenkinsAccess jenAcc, String jenkinsUrl, String jobname, SimpleDateFormat sdf) {
        jenkinsAccess = jenAcc;
        standardUrl = jenkinsUrl + "/job/" + jobname + "/job/";
        dateformat = sdf;
    }

    public void startBuildingReport(){
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

            staticPreCode(bwr);

            buildTable(trunk, bwr, "TRUNK");
            buildTable(rc, bwr, "RC");
            buildTable(rc2, bwr, "RC2");
            buildTable(bf, bwr, "BF");
            buildTable(uat4, bwr, "UAT4");

            staticPostCode(bwr);

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

    private void staticPostCode(BufferedWriter bwr) throws IOException {
        bwr.write("<p style=font-size:20px><b>Sonar (Trunk) - <a href=" + sonar + "><u>Link</u></a></b></p>");
        bwr.newLine();
        bwr.write("<p style=font-size:20px><b>Code Coverage - <a href=" + codecove + ">Link</a></b></p>");
        bwr.newLine();
        bwr.write("<p>Excel-Sheet</br>unter <a href=" + excelpath
            + ">P:\\LOB\\CC_INFONOVA_SOFTWARE_ENGINEERING\\OpenNet_CI\\Failed_Tests.xlsx</a></p>");
        bwr.newLine();
        int i = 1;
        boolean bool = false;
        for (Job j : jobClassList) {
            for (Failure f : failList) {
                int custlength = 160;
                if (f.getFailure().length() < 160) {
                    custlength = f.getFailure().length();
                }
                if (bool && !f.getFailure().equals("NoFailure") && f.getJobName().equals(j.getJobName())) {
                    bwr.write(" ,</br><b>Testname: " + f.getClassname() + "  </br>Fehler:</b> "
                        + f.getFailure().substring(0, custlength));
                } else if (f.getJobName().equals(j.getJobName()) && !f.getFailure().equals("NoFailure")) {
                    bwr.write("<p style=font-size:16px><sup>" + i + "</sup> <b>Testname: " + f.getClassname()
                        + "  </br>Fehler:</b> " + f.getFailure().substring(0, custlength) + "</br>");
                    bool = true;
                }

            }
            if (bool) {
                i++;

            }
            bool = false;
            bwr.write("</p>");
            bwr.newLine();
        }

        bwr.write("</html>");
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

    private void staticPreCode(BufferedWriter bwr) throws IOException {
        bwr.write("<!DOCTYPE html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>");
        bwr.newLine();
        bwr.write("<style type=\"text/css\">\n"
            + ".tg  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#ccc;}\n"
            + ".tg td{font-weight:bold;font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}\n"
            + ".tg th{font-family:Arial, sans-serif;font-size:18px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal}\n"
            + ".tg .tg-TRUNKh{font-weight:bold;font-size:36px;background-color:#4875cc;text-align:center}\n"
            + ".tg .tg-TRUNKth{background-color:#bfd2f7;font-size:18px;border-color:#aabcfe}\n"
            + ".tg .tg-TRUNKtd{border-color:#aabcfe;background-color:#e8edff;}\n" +

            ".tg .tg-RCh{font-weight:bold;font-size:36px;background-color:#f56b00}\n"
            + ".tg .tg-RCth{background-color:#FCFBE3;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-RCtd{background-color:#fff;border-color:#ccc;}" +

            ".tg .tg-BFh{font-weight:bold;font-size:36px;background-color:#0aba0b}\n"
            + ".tg .tg-BFth{background-color:#C2FFD6;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-BFtd{border-color:#bbb;background-color:#E0FFEB;}" +

            ".tg .tg-UAT4h{font-weight:bold;font-size:36px;background-color:#c0c0c0}\n"
            + ".tg .tg-UAT4th{background-color:#efefef;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-UAT4td{border-color:#ccc;background-color:#fff;}" +

            ".tg .tg-RC2h{font-weight:bold;font-size:36px;background-color:#674421}\n"
            + ".tg .tg-RC2th{background-color:#bd9c7b;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-RC2td{border-color:#ccc;background-color:#f7ece0;}\n" +

            ".tg .tg-failtd{border-color:#ccc;background-color:#ff8f8f;font-size:18px;}" +

            "</style>");
        bwr.newLine();
    }

    private void buildTable(List<Job> list, BufferedWriter bwr, String name) throws IOException {
        bwr.write("<table class=tg><tr><th class=tg-" + name + "h colspan=" + 3 + ">" + name + "</th></tr>");
        bwr.newLine();
        bwr.write("<tr><td class=tg-" + name + "th>Step</td><td class=tg-" + name + "th>Ergebnis</td><td class=tg-"
            + name + "th>Zuletzt gr&uuml;n</td></tr>");
        bwr.newLine();

        String hilf = name;
        String hoch = "";
        boolean bool = false;
        for (Job r : list) {
            for (Failure f : failList) {
                if (bool) {

                } else if (f.getJobName().equals(r.getJobName()) && !f.getFailure().equals("NoFailure")) {
                    starthoch++;
                    hoch = "" + starthoch;
                    bool = true;
                }

            }
            if (r.getResult().equals("OK") || (r.getResult().contains("NOK"))) {
                name = hilf;
            } else {
                name = "fail";
            }

            bwr.write("<tr><td class=tg-" + name + "td><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                + r.getJobName() + ">" + r.getJobName() + "</a></td></a><td class=tg-" + name + "td>" + r.getResult()
                + "<sup>" + hoch + "</sup>" + "</td><td class=tg-" + name + "td>" + r.getLastStableDate()
                + "</td></tr>");
            bwr.newLine();
            hoch = "";
            bool = false;
        }
        bwr.write("</table></br><p>.</p>");
        bwr.newLine();
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
