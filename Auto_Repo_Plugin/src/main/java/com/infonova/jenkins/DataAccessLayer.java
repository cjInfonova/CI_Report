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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
