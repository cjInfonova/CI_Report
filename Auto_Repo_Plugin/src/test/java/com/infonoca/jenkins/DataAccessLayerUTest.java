package com.infonoca.jenkins;

import com.infonova.easymock.EasyMockRule;
import com.infonova.easymock.annotation.UnitToTest;
import com.infonova.jenkins.DataAccessLayer;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.easymock.internal.MocksControl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import sun.net.www.http.HttpClient;

import javax.wsdl.Input;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collections;

import static org.easymock.EasyMock.expect;

/**
 * Created by dominic.gross on 21.07.2015.
 */
public class DataAccessLayerUTest {

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @UnitToTest
    private DataAccessLayer dal;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public final String JOB_NAME = "A1OpenNet";
    public final String STANDARD_URL = "https://ci.infonova.at/job/" + JOB_NAME + "/job/";
    public final String JENKINS_URL = "https://ci.infonova.at";
    public final String JSON_EXTENTION = "/api/json";
    private final String LAST_STATE = "/lastBuild";
    private final String STABLE_STATE = "/lastStableBuild";
    private final String USERNAME = "max.mustermann";
    private final String PASSWORD = "password";

    @Before
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
