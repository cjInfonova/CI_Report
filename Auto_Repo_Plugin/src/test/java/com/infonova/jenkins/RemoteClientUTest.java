package com.infonova.jenkins;

import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by christian.jahrbacher on 03.08.2015.
 */
public class RemoteClientUTest extends EasyMockSupport {

    private RemoteClient remoteClient;

    private HttpHost host;
    private AuthCache authCache;
    private CloseableHttpClient httpClient;
    private final static String username = "christian.jahrbacher";
    private final static String password = "Cj170615!";
    public String JENKINS_URL = "https://ci.infonova.at";

    @Before
    @Ignore
    public void setup() {
        URI uri = URI.create(JENKINS_URL);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(
            username, password));
        authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        authCache.put(host, basicAuth);
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    @Test(expected = IllegalStateException.class)
    @Ignore
    public void getJsonNodeFromUrlWithInvalidCredentials() {
        remoteClient = new RemoteClient(JENKINS_URL, username, "wrong password", JENKINS_URL + "/job/A1OpenNet/job/");
        try {
            remoteClient
                .getJsonNodeFromUrl(JENKINS_URL + "/job/A1OpenNet/job/A1ON-java-build-trunk/lastBuild/api/json");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        URI uri = URI.create(JENKINS_URL + "/job/A1OpenNet/job/A1ON-java-build-trunk/lastBuild/api/json");
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        CloseableHttpResponse response = createMock(CloseableHttpResponse.class);
        response.setStatusCode(401);
        try {
            expect(httpClient.execute(host, httpGet, localContext)).andReturn(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        expect(response.getStatusLine().getStatusCode() == 401);
        expect(
            new RemoteException(response.getStatusLine().getStatusCode() + ": Username and/or Password is incorrect!"))
            .andThrow(new RemoteException());
    }

    @Test(expected = IllegalStateException.class)
    @Ignore
    public void getJsonNodeFromUrlWithInvalidUrl() {
        remoteClient = new RemoteClient(JENKINS_URL, username, password, JENKINS_URL + "/job/A1OpenNet/job/");
        try {
            remoteClient.getJsonNodeFromUrl(JENKINS_URL + "/job/A1OpenNet/job/InvalidProject/lastBuild/api/json");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        URI uri = URI.create(JENKINS_URL + "/job/A1OpenNet/job/InvalidProject/lastBuild/api/json");
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        CloseableHttpResponse response = createMock(CloseableHttpResponse.class);
        response.setStatusCode(404);
        try {
            expect(httpClient.execute(host, httpGet, localContext)).andReturn(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        expect(response.getStatusLine().getStatusCode() == 404);
        expect(new RemoteException(response.getStatusLine().getStatusCode() + "")).andThrow(new RemoteException());
    }

    @Test(expected = IllegalStateException.class)
    @Ignore
    public void getJsonNodeFromUrlWithValidSettings() {
        remoteClient = new RemoteClient(JENKINS_URL, username, password, JENKINS_URL + "/job/A1OpenNet/job/");
        try {
            remoteClient
                .getJsonNodeFromUrl(JENKINS_URL + "/job/A1OpenNet/job/A1ON-java-build-trunk/lastBuild/api/json");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        URI uri = URI.create(JENKINS_URL + "/job/A1OpenNet/A1ON-java-build-trunk/build/lastBuild/api/json");
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        CloseableHttpResponse response = createMock(CloseableHttpResponse.class);
        response.setStatusCode(200);
        response.setHeader(new BasicHeader("Content-Type", "json"));
        try {
            expect(httpClient.execute(host, httpGet, localContext)).andReturn(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        expect(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300
            && response.getFirstHeader("Content-Type").getValue().contains("json"));
        try {
            expect(new ObjectMapper().readTree(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
