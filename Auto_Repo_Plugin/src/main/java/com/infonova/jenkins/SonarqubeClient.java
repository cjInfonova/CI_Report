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

        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSslcontext( sslContext);

        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
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

    // trusting all certificate
    public void doTrustToCertificates() throws Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                return;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                return;
            }
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {

            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '"
                        + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    /*
     * Besser wäre es hier die Konvertierung von Externer und Interner Datensturktur in nur einer
     * klasse zu mappen.
     * 
     * mein Vorschlag:
     * TODO:
     * 
     * public Job getJobStatus(String jobname)....
     * 
     * public List<Failure> getJobFailures(String jobname) ....
     */

    public void getJsonNodeFromUrl() throws IOException {
        System.out.println("Start preparing getJsonNode");
        try {
            //doTrustToCertificates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        URI uri = URI.create(connectionUrl);
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        System.out.println("Do execute");
        try {
            //doTrustToCertificates();
            HttpResponse response = httpClient.execute(host, httpGet, localContext);
            //doTrustToCertificates();
            System.out.println("Did execute");

            System.out.println("Statusline: " + response.getStatusLine().getStatusCode());
            System.out.println("Statusline: " + response.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }
}
