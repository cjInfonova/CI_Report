package com.infonova.jenkins;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.maven.plugins.annotations.Mojo;
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
@Mojo(name = "report_dal")
public class DataAccessLayer {

    private List<String> jobList;
    private List<ReportType> repoTypeList;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public final String JOB_NAME = "A1OpenNet";
    public final String STANDARD_URL = "https://ci.infonova.at/job/" + JOB_NAME + "/job/";
    public final String JSON_EXTENTION = "/api/json";
    private final String LAST_STATE = "/lastBuild";
    private final String STABLE_STATE = "/lastStableBuild";
    private final String USERNAME = "dominic.gross";
    private final String PASSWORD = "Dg230615!";
    private static Logger log = Logger.getLogger("MyLogger");

    public DataAccessLayer() {
        setupJobList();
        getAllJsonsFromJenkins();
        showReports();
        generateHTML();
    }

    private void generateHTML() {
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
            System.out.println(f.canWrite());
            FileWriter fr = new FileWriter(f);
            bwr = new BufferedWriter(fr);

            bwr.write("<DOCTYPE html>");
            bwr.newLine();
            bwr.write("<style type=\"text/css\">\n"
                + ".tg  {border-collapse:collapse;border-spacing:0;border-color:#aabcfe;}\n"
                + ".tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aabcfe;background-color:#e8edff;}\n"
                + ".tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aabcfe;background-color:#b9c9fe;}\n"
                + ".tg .tg-uebf{font-weight:bold;font-size:28px;background-color:#4875cc;text-align:center}\n"
                + ".tg .tg-8rb9{font-weight:bold;font-size:20px;background-color:#7fca88;text-align:center}\n"
                + ".tg .tg-pczq{background-color:#bfd2f7;font-size:16px}\n"
                + ".tg .tg-0e45{font-weight:bold;font-size:13px}\n"
                +

                ".tg2  {border-collapse:collapse;border-spacing:0;border-color:#aaa;}\n"
                + ".tg2 td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aaa;background-color:#fff;}\n"
                + ".tg2 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#aaa;background-color:#f38630;}\n"
                + ".tg2 .tg-c3sw2{font-weight:bold;font-size:28px;background-color:#f56b00}\n"
                + ".tg2 .tg-jws92{font-weight:bold;font-size:18px;background-color:#f56b00}\n"
                + ".tg2 .tg-o80d2{background-color:#FCFBE3;font-size:16px}\n"
                + ".tg2 .tg-0e452{font-weight:bold;font-size:13px}"
                +

                ".tg3  {border-collapse:collapse;border-spacing:0;border-color:#bbb;}\n"
                + ".tg3 td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#bbb;background-color:#E0FFEB;}\n"
                + ".tg3 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#bbb;background-color:#9DE0AD;}\n"
                + ".tg3 .tg-c487{font-weight:bold;font-size:28px;background-color:#0aba0b}\n"
                + ".tg3 .tg-iqb1{background-color:#C2FFD6;font-size:16px}\n"
                + ".tg3 .tg-0e453{font-weight:bold;font-size:13px}"
                +

                ".tg4  {border-collapse:collapse;border-spacing:0;border-color:#ccc;}\n"
                + ".tg4 td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;background-color:#fff;}\n"
                + ".tg4 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;background-color:#f0f0f0;}\n"
                + ".tg4 .tg-h098{font-weight:bold;font-size:28px;background-color:#c0c0c0}\n"
                + ".tg4 .tg-47f3{background-color:#efefef;font-size:16px}\n"
                + ".tg4 .tg-0e454{font-weight:bold;font-size:13px}" +

                "</style>");
            bwr.newLine();


            bwr.write("<table class=tg><tr><th class=tg-uebf colspan=" + 3
                + ">TRUNK</th></tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-pczq>Step</td><td class=tg-pczq>Ergebnis</td><td class=tg-pczq>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : trunk) {
                bwr.write("<tr><td class=tg-0e45><a href=https://ci.infonova.at/job/A1OpenNet/job/" + r.getReportName()
                    + ">" + r.getReportName() + "</a></td></a><td class=tg-0e45>" + r.getResult()
                    + "</td><td class=tg-0e45>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br>");
            bwr.newLine();

            bwr.write("<table class=tg2><tr><th class=tg-c3sw2 colspan=" + 3
                + ">RC</th></tr><tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-o80d2>Step</td><td class=tg-o80d2>Ergebnis</td><td class=tg-o80d2>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : rc) {
                bwr.write("<tr><td class=tg-0e452><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getReportName() + ">" + r.getReportName() + "</a></td></a><td class=tg-0e452>" + r.getResult()
                    + "</td><td class=tg-0e452>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br>");
            bwr.newLine();

            bwr.write("<table class=tg3><tr><th class=tg-c487 colspan=" + 3 + ">BF</th></tr><tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-iqb1>Step</td><td class=tg-iqb1>Ergebnis</td><td class=tg-iqb1>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : bf) {
                bwr.write("<tr><td class=tg-0e453><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getReportName() + ">" + r.getReportName() + "</a></td></a><td class=tg-0e453>" + r.getResult()
                    + "</td><td class=tg-0e453>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br>");
            bwr.newLine();

            bwr.write("<table class=tg4><tr><th class=tg-h098 colspan=" + 3 + ">UAT4</th></tr><tr>");
            bwr.newLine();
            bwr.write("<tr><td class=tg-47f3>Step</td><td class=tg-47f3>Ergebnis</td><td class=tg-47f3>Zuletzt grün</td></tr>");
            bwr.newLine();
            for (ReportType r : uat4) {
                bwr.write("<tr><td class=tg-0e454><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getReportName() + ">" + r.getReportName() + "</a></td></a><td class=tg-0e454>" + r.getResult()
                    + "</td><td class=tg-0e454>"+r.getLastStableDate()+"</td></tr>");
                bwr.newLine();

            }
            bwr.write("</table></br>");
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

    public void getAllJsonsFromJenkins() {
        repoTypeList = new ArrayList<ReportType>();
        for (String job : jobList) {
            try {
                JSONObject jo = startConnectionToJenkins(job, LAST_STATE);
                String[] tempDataArray = getDataFromJson(jo);
                if (tempDataArray[0].equals("SUCCESS")) {
                    repoTypeList.add(new ReportType(job, tempDataArray[0],
                        DatatypeConverter.parseInt(tempDataArray[1]), DatatypeConverter.parseInt(tempDataArray[2]),
                        tempDataArray[3]));
                } else {
                    JSONObject lastStableJS = startConnectionToJenkins(job, STABLE_STATE);
                    String lastStableDate = simpleDateFormat.format(getDateFromJSon(lastStableJS));
                    // String lastStableDate = lastStableJS.getString("id");
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
        // strArray = new String[] { jo.getString("result"), "0", "0", jo.getString("id") };

        JSONArray jArray = jo.getJSONArray("actions");
        for (int i = 0; i < jArray.length(); i++) {
            if (jArray.get(i).getClass() == JSONObject.class && jArray.getJSONObject(i) != null
                && jArray.getJSONObject(i).length() != 0) {

                if (jArray.getJSONObject(i).has("failCount")) {

                    try {
                        // log.info("--------------------------------------------------------------------");
                        if (strArray[1].equals("0")) {
                            strArray[1] = jArray.getJSONObject(i).getString("failCount");
                        }
                        if (strArray[2].equals("0")) {
                            strArray[2] = jArray.getJSONObject(i).getString("totalCount");
                        }
                        // log.info("--------------------------------------------------------------------");
                    } catch (JSONException ex) {
                        System.out.println("Hier war der Fehler");
                        log.info("----------------------JSONException geworfen------------------------");
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

    private JSONObject startConnectionToJenkins(String job, String state) throws IOException, JSONException {
        // authentification for Jenkins
        InputStream inputStream = scrape(STANDARD_URL + job + state + JSON_EXTENTION, USERNAME, PASSWORD);
        // log.info(STANDARD_URL+job+JSON_EXTENTION);
        JSONObject jo = new JSONObject();
        try {
            jo = inputToJSon(inputStream);
        } catch (JSONException jsexe) {
            log.info("An unexpected error has occurred!");
            // jsexe.printStackTrace();
        } finally {
            inputStream.close();
        }
        return jo;
    }

    private JSONObject inputToJSon(InputStream inSt) throws JSONException {
        String fullJSon = "";
        try {
            InputStreamReader isr = new InputStreamReader(inSt);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                // log.info(line);
                fullJSon += line;
            }
        } catch (IOException exe) {
            log.info("An unexpected error has occurred!");
            exe.printStackTrace();
        }

        return new JSONObject(fullJSon);
    }

    private InputStream scrape(String urlString, String username, String password)
            throws ClientProtocolException, IOException {
        // log.info("authentication in Jenkins");
        URI uri = URI.create(urlString);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(
            username, password));
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        HttpGet httpGet = new HttpGet(uri);
        // Add AuthCache to the execution context
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        HttpResponse response = httpClient.execute(host, httpGet, localContext);

        return response.getEntity().getContent();
    }
}
