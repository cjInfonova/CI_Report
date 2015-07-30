package com.infonova.jenkins;

import org.apache.http.impl.client.CloseableHttpClient;
import sun.net.www.http.HttpClient;

/**
 * Created by christian.jahrbacher on 27.07.2015.
 */
public class JenkinsClient {

    CloseableHttpClient httpClient;

    public JenkinsClient(String url, String user, String password){
        //....
    }

    public Object reciveJobdetails(String jobName){
        return new Object();
    }
}
