package com.infonova.jenkins;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

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
        setupLoginUser(uri);
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    public JenkinsClient(String url, String user, String password){
        URI uri = URI.create(url);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(user,
                password));
        setupLoginUser(uri);
        try {
            httpClient = createHttpClient_AcceptsUntrustedCerts(credsProvider);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void setupLoginUser(URI uri){
        authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        authCache.put(host, basicAuth);
    }

    public CloseableHttpClient createHttpClient_AcceptsUntrustedCerts(CredentialsProvider credProv) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder b = HttpClientBuilder.create();

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        //b.setSslcontext( sslContext);

        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        b.setConnectionManager(connMgr);
        b.setDefaultCredentialsProvider(credProv);

        CloseableHttpClient client = b.build();
        return client;
    }

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
