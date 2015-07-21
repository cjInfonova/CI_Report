package com.infonova.jenkins;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by christian.jahrbacher on 15.07.2015.
 */
// @Mojo(name = "report_dal")
public class DataAccessLayer {

    private List<String> jobList;
    private List<ReportType> repoTypeList;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public final String JOB_NAME = "A1OpenNet";
    public final String STANDARD_URL = "https://ci.infonova.at/job/" + JOB_NAME + "/job/";
    public final String JENKINS_URL = "https://ci.infonova.at";
    public final String JSON_EXTENTION = "/api/json";
    private final String LAST_STATE = "/lastBuild";
    private final String STABLE_STATE = "/lastStableBuild";
    private final String USERNAME = "dominic.gross";
    private final String PASSWORD = "Dg230615!";
    private static Logger log = Logger.getLogger("MyLogger");

    public void generateHTML() {
        BufferedWriter bwr = null;
        try {
            List<ReportType> uat4 = new ArrayList<ReportType>();
            List<ReportType> trunk = new ArrayList<ReportType>();
            List<ReportType> rc = new ArrayList<ReportType>();
            List<ReportType> bf = new ArrayList<ReportType>();
            for (ReportType r : repoTypeList) {
                String[] split = r.getReportName().split("-");

                if (split[split.length - 1].equals("trunk") || split[split.length - 1].equals("trunk12c")) {
                    trunk.add(r);
                }
                if (split[split.length - 1].equals("rc")) {
                    rc.add(r);
                }
                if (split[split.length - 1].equals("UAT4")) {
                    uat4.add(r);
                }
                if (split[split.length - 1].equals("bf")) {
                    bf.add(r);
                }
            }
            File f = new File("data.html");
            FileWriter fr = new FileWriter(f);
            bwr = new BufferedWriter(fr);

            bwr.write("<DOCTYPE html>");
            bwr.newLine();
            bwr.write("<style type=\"text/css\">\n"
                + ".tg  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#aabcfe;}\n"
                + ".tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aabcfe;background-color:#e8edff;}\n"
                + ".tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aabcfe;background-color:#b9c9fe;}\n"
                + ".tg .tg-h1{font-weight:bold;font-size:36px;background-color:#4875cc;text-align:center}\n"
                + ".tg .tg-th1{background-color:#bfd2f7;font-size:18px}\n"
                + ".tg .tg-td1{font-weight:bold;font-size:14px}\n"
                +

                ".tg2  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#aaa;}\n"
                + ".tg2 td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aaa;background-color:#fff;}\n"
                + ".tg2 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aaa;background-color:#f38630;}\n"
                + ".tg2 .tg-h2{font-weight:bold;font-size:36px;background-color:#f56b00}\n"
                + ".tg2 .tg-th2{background-color:#FCFBE3;font-size:18px}\n"
                + ".tg2 .tg-td2{font-weight:bold;font-size:14px}"
                +

                ".tg3  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#bbb;}\n"
                + ".tg3 td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#bbb;background-color:#E0FFEB;}\n"
                + ".tg3 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#bbb;background-color:#9DE0AD;}\n"
                + ".tg3 .tg-h3{font-weight:bold;font-size:36px;background-color:#0aba0b}\n"
                + ".tg3 .tg-th3{background-color:#C2FFD6;font-size:18px}\n"
                + ".tg3 .tg-td3{font-weight:bold;font-size:14px}"
                +

                ".tg4  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#ccc;}\n"
                + ".tg4 td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;background-color:#fff;}\n"
                + ".tg4 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;background-color:#f0f0f0;}\n"
                + ".tg4 .tg-h4{font-weight:bold;font-size:36px;background-color:#c0c0c0}\n"
                + ".tg4 .tg-th4{background-color:#efefef;font-size:18px}\n"
                + ".tg4 .tg-td4{font-weight:bold;font-size:14px}" +

                "</style>");
            bwr.newLine();


            bwr.write("<table class=tg><tr><th class=tg-h1 colspan=" + 3
                + ">TRUNK</th></tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-th1>Step</td><td class=tg-th1>Ergebnis</td><td class=tg-th1>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : trunk) {
                bwr.write("<tr><td class=tg-td1><a href=https://ci.infonova.at/job/A1OpenNet/job/" + r.getReportName()
                    + ">" + r.getReportName() + "</a></td></a><td class=tg-td1>" + r.getResult()
                    + "</td><td class=tg-td1>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br><p>.</p>");
            bwr.newLine();

            bwr.write("<table class=tg2><tr><th class=tg-h2 colspan=" + 3
                + ">RC</th></tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-th2>Step</td><td class=tg-th2>Ergebnis</td><td class=tg-th2>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : rc) {
                bwr.write("<tr><td class=tg-td2><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getReportName() + ">" + r.getReportName() + "</a></td></a><td class=tg-td2>" + r.getResult()
                    + "</td><td class=tg-td2>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br><p>.</p>");
            bwr.newLine();

            bwr.write("<table class=tg3><tr><th class=tg-h3 colspan=" + 3 + ">BF</th></tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-th3>Step</td><td class=tg-th3>Ergebnis</td><td class=tg-th3>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : bf) {
                bwr.write("<tr><td class=tg-td3><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getReportName() + ">" + r.getReportName() + "</a></td></a><td class=tg-td3>" + r.getResult()
                    + "</td><td class=tg-td3>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br><p>.</p>");
            bwr.newLine();

            bwr.write("<table class=tg4><tr><th class=tg-h4 colspan=" + 3 + ">UAT4</th></tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-th4>Step</td><td class=tg-th4>Ergebnis</td><td class=tg-th4>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : uat4) {
                bwr.write("<tr><td class=tg-td4><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getReportName() + ">" + r.getReportName() + "</a></td></a><td class=tg-td4>" + r.getResult()
                    + "</td><td class=tg-td4>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br><p>.</p>");
            bwr.newLine();

            bwr.write("</html>");
            bwr.newLine();
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

    public void setupJobList() {

        jobList = new ArrayList<String>();
        // Trunk12c
        jobList.add("A1ON-java-build-trunk");
        jobList.add("A1ON-jtf-livetests-trunk12c");
        jobList.add("A1ON-jtf-db-infrastructure-trunk12c");
        jobList.add("A1ON-jtf-db-regressiontests-trunk12c");
        jobList.add("A1ON-jtf-db-guidelines-trunk12c");
        jobList.add("A1ON-jtf-project-tests-trunk12c");
        jobList.add("A1ON-jtf-regressiontests-trunk12c");
        // jobList.add("A1ON-jtf-smoketests-trunk12c");
        // RC2
        //
        // * CLOSED
        // * jobList.add("A1ON-java-build-rc2");
        // * jobList.add("A1ON-jtf-db-guidelines-rc2");
        // * jobList.add("A1ON-jtf-db-regressiontests-rc2");
        // * jobList.add("A1ON-jtf-regressiontests-rc2");
        // * jobList.add("A1ON-jtf-smoketests-rc2");
        //
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
        for (ReportType rt : repoTypeList) {
            System.out.println(rt.toString());
        }
    }

    public void getAllReportsFromJenkins() {
        repoTypeList = new ArrayList<ReportType>();

        URI uri = URI.create(JENKINS_URL);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(
                USERNAME, PASSWORD));
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

        for (String job : jobList) {
            try {
                JSONObject jo = startConnectionToJenkins(job, LAST_STATE, host, authCache, httpClient);
                String[] tempDataArray = getDataFromJson(jo);
                if (tempDataArray[0].equals("SUCCESS")) {
                    repoTypeList.add(new ReportType(job, tempDataArray[0],
                            DatatypeConverter.parseInt(tempDataArray[1]), DatatypeConverter.parseInt(tempDataArray[2]),
                            tempDataArray[3]));
                } else {
                    JSONObject lastStableJS = startConnectionToJenkins(job, STABLE_STATE, host, authCache, httpClient);
                    String lastStableDate = simpleDateFormat.format(getDateFromJSon(lastStableJS));
                    repoTypeList.add(new ReportType(job, tempDataArray[0],
                            DatatypeConverter.parseInt(tempDataArray[1]), DatatypeConverter.parseInt(tempDataArray[2]),
                            lastStableDate));
                }

            } catch (JSONException jsexe) {

                log.info("The requested resource is not available. Jobname: " + job);
                jsexe.printStackTrace();
            } catch (ParseException pexe) {
                log.info("Something went wrong while parsing ...");
            } catch (IOException exe) {
                log.info("An unexpected error has occurred");
                exe.printStackTrace();
            }
        }
        log.info("Reports konnten geladen werden: " + repoTypeList.size() + "/" + jobList.size());
    }

    private String[] getDataFromJson(JSONObject jo) throws JSONException, ParseException {
        String[] strArray = new String[] { "", "0", "0", "0" };;
        if (jo.getBoolean("building")) {
            strArray = new String[] { "RUNNING", "0", "0", "0" };
            return strArray;
        }
        strArray = new String[] { jo.getString("result"), "0", "0", simpleDateFormat.format(getDateFromJSon(jo)) };

        JSONArray jArray = jo.getJSONArray("actions");
        for (int i = 0; i < jArray.length(); i++) {
            if (jArray.get(i).getClass() == JSONObject.class && jArray.getJSONObject(i) != null
                    && jArray.getJSONObject(i).length() != 0) {

                if (jArray.getJSONObject(i).has("failCount")) {

                    try {
                        if (strArray[1].equals("0")) {
                            strArray[1] = jArray.getJSONObject(i).getString("failCount");
                        }
                        if (strArray[2].equals("0")) {
                            strArray[2] = jArray.getJSONObject(i).getString("totalCount");
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return strArray;
    }

    private Date getDateFromJSon(JSONObject jo) throws JSONException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(jo.getString("id"));
    }

    private JSONObject startConnectionToJenkins(String job, String state, HttpHost host, AuthCache authCache, CloseableHttpClient httpClient) throws IOException, JSONException {
        InputStream inputStream = getJsonFromUrl(STANDARD_URL + job + state + JSON_EXTENTION, host, authCache, httpClient);
        JSONObject jo = new JSONObject();
        try {
            jo = inputStreamToJSon(inputStream);
        } catch (JSONException jsexe) {
            log.info("An unexpected error has occurred!");
            jsexe.printStackTrace();
        } finally {
            inputStream.close();
        }
        return jo;
    }

    private JSONObject inputStreamToJSon(InputStream inSt) throws JSONException {
        String fullJSon = "";
        try {
            InputStreamReader isr = new InputStreamReader(inSt);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                fullJSon += line;
            }
        } catch (IOException exe) {
            log.info("An unexpected error has occurred!");
            exe.printStackTrace();
        }

        return new JSONObject(fullJSon);
    }

    private InputStream getJsonFromUrl(String urlString, HttpHost host, AuthCache authCache, CloseableHttpClient httpClient)
            throws ClientProtocolException, IOException {

        URI uri = URI.create(urlString);
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        return httpClient.execute(host, httpGet, localContext).getEntity().getContent();
    }
}
