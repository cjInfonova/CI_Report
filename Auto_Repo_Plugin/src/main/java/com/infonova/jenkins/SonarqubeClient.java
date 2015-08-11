package com.infonova.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import sun.net.www.http.HttpClient;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by christian.jahrbacher on 10.08.2015.
 */
public class SonarqubeClient {

    private HttpHost host;
    private AuthCache authCache;
    private CloseableHttpClient httpClient;
    private String connectionUrl;

    public SonarqubeClient(String url, String user, String password, String connectionUrl) {
        this.connectionUrl = connectionUrl;
        System.out.println("Prepare SonarqubeLogin");
        URI uri = URI.create(url);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(user,
            password));
        authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        authCache.put(host, basicAuth);

        //httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        try {
            httpClient = createHttpClient_AcceptsUntrustedCerts(credsProvider);
            //doTrustToCertificates();
            HttpURLConnection conn = (HttpURLConnection)uri.toURL().openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finish preparing login");
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

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        b.setConnectionManager(connMgr);
        b.setDefaultCredentialsProvider(credProv);

        // finally, build the HttpClient;
        //      -- done!
        CloseableHttpClient client = b.build();
        return client;
    }

    public JsonNode getJsonNodeFromUrl(String link) throws IOException, JenkinsException {
        System.out.println("Start preparing getJsonNode");
        link = "https://grzisesonar1.infonova.at/api/resources?resource=com.bearingpoint.ta:opennet&includetrends=true&includealerts=true&format=json&period=2&metrics=new_coverage,ncloc,coverage,lines,files,statements,directories,classes,functions,accessors,open_issues,sqale_index,new_technical_debt,blocker_violations,critical_violations,major_violations,minor_violations,new_violations,info_violations";
        try {
            //doTrustToCertificates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        URI uri = URI.create(link);
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        System.out.println("Do execute");
        try {
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

            //System.out.println("Did execute");

            //System.out.println("Statusline: " + response.getStatusLine().getStatusCode());
            //System.out.println("Statusline: " + response.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("LOL");
        }
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }
}
