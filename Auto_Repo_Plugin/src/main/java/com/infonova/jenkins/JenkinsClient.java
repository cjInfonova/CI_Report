package com.infonova.jenkins;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by christian.jahrbacher on 28.07.2015.
 */
public class JenkinsClient {
    //TODO: JenkinsClient ist ein schlechter name für einen solche klasse: Mein Vorschläge JenkinsClient oder nur Jenkins

    private HttpHost host;
    private AuthCache authCache;
    private CloseableHttpClient httpClient;
    private String connectionUrl;

    public JenkinsClient(String url, String user, String password, String connectionUrl) {
        this.connectionUrl = connectionUrl;
        URI uri = URI.create(url);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(user,
            password));
        authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        authCache.put(host, basicAuth);
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    /*
    Besser wäre es hier die Konvertierung von Externer und Interner Datensturktur in nur einer
    klasse zu mappen.

    mein Vorschlag:
    TODO:

    public Job getJobStatus(String jobname)....

    public List<Failure> getJobFailures(String jobname) ....
     */

    public JsonNode getJsonNodeFromUrl(String urlString) throws IOException, JenkinsException {
        URI uri = URI.create(urlString);
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        HttpResponse response = httpClient.execute(host, httpGet, localContext);

        if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300
            && response.getFirstHeader("Content-Type").getValue().contains("json")) {
            return new ObjectMapper().readTree(response.getEntity().getContent());
        } else if (response.getStatusLine().getStatusCode() == 401) {
            throw new JenkinsException(response.getStatusLine().getStatusCode()
                + ": Username and/or Password is incorrect!");
        } else if (response.getStatusLine().getStatusCode() == 404) {
            throw new JenkinsException(response.getStatusLine().getStatusCode() + ": Source not found!");
        } else {
            throw new JenkinsException(response.getStatusLine().getStatusCode() + ": "
                + response.getStatusLine().getReasonPhrase());
        }
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }
}
