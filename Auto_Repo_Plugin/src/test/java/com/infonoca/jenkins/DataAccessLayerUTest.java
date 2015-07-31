package com.infonoca.jenkins;

import com.infonova.easymock.EasyMockRule;
import com.infonova.easymock.annotation.Mock;
import com.infonova.easymock.annotation.UnitToTest;
import com.infonova.jenkins.*;
import org.jbpm.identity.security.Username;
import org.junit.Rule;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

/**
 * Created by dominic.gross on 21.07.2015.
 */
public class DataAccessLayerUTest {

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @UnitToTest
    private DataAccessLayer dal;
    @Mock (inject = false)
    private static JenkinsAccess jenkinsAccess;
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public final static String JOB_NAME = "A1OpenNet";
    public final static String STANDARD_URL = "https://ci.infonova.at/job/" + JOB_NAME + "/job/";
    @Mock
    private static HTML_Generator htmlgen;
    @Mock
    private  static JobBuilder jobBuilder;
//    public final static String JENKINS_URL = "https://ci.infonova.at";
//    public final static String JSON_EXTENTION = "/api/json";
//    private final static String LAST_STATE = "/lastBuild";
//    private final static String STABLE_STATE = "/lastStableBuild";
//    private final static String USERNAME = "dominic.gross";
//    private final static String PASSWORD = "Dg230615!";
//
//    @Mock
//    private List<JenkinsSystem> jenkinsSystemList;
//
//
//    @Mock
//    private List<String> jobList;
//    @Mock
//    private List<Job> jobClassList;
//    @Mock
//    private List<Failure> failList;
//    private final static Logger log = Logger.getLogger("MyLogger");
//    private final static String sonar = "https://grzisesonar1.infonova.at/drilldown/issues/100648?period=2";
//    private final static String codecove = "https://grzisesonar1.infonova.at/dashboard/index/100648?did=1&period=2";
//    @Mock
//    private static JenkinsAccess jenkinsAccess = new JenkinsAccess(STANDARD_URL,USERNAME,PASSWORD);
//    @Mock
//    private HTML_Generator htmlgen;
    //private final static JobBuilder jobBuilder = new JobBuilder(jenkinsAccess,STANDARD_URL,simpleDateFormat);



    @Test
    public void generateHTML()  {
//        BufferedWriter bwr = null;
//        List<Job> somelist = initList("");
//        File f = new File("data.html");
//        FileWriter fr = new FileWriter(f);
//        bwr = new BufferedWriter(fr);
//        htmlgen.staticPreCode(bwr);
//        expectLastCall();

        //htmlgen.buildTable(somelist, bwr, "TRUNK", failList);
//        htmlgen.buildTable(somelist, bwr, "TRUNK", failList);
//        htmlgen.buildTable(somelist, bwr, "TRUNK", failList);
//        htmlgen.buildTable(somelist, bwr, "TRUNK", failList);

//        htmlgen.staticPostCode(bwr, jobClassList, failList, sonar, codecove);
    }

//    private List<Job> initList(String... name) {
//        List<Job> list = new ArrayList<Job>();
//        for (Job r : jobClassList) {
//            String[] split = r.getJobName().split("-");
//            for (String s : name) {
//                if (split[split.length - 1].equals(s)) {
//                    list.add(r);
//                }
//            }
//        }
//        return list;
//
//
//    }
    /*@Before
    public void setupDAL()
    {
        dal = new DataAccessLayer();
        dal.setupJobList();
    }

    /*@Test(expected = IOException.class)
    public void connectToJenkinsWithWrongCredentials() throws IOException {
        URI uri = URI.create(STANDARD_URL);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials("max.muster", "password"));
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProv).build();
        uri = URI.create("A1ON-java-build-trunk");
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext locCon = HttpClientContext.create();
        locCon.setAuthCache(authCache);
        InputStream inStream = new ByteArrayInputStream(new byte[123]);

        expect(httpClient.execute(host, httpGet, locCon).getEntity().getContent()).andReturn(inStream);

        dal.getAllReportsFromJenkins();
    }*/
}
