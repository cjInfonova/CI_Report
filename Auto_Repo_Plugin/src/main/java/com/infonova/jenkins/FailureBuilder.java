package com.infonova.jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by christian.jahrbacher on 30.07.2015.
 */
public class FailureBuilder implements UrlParameter {

    private RemoteClient remoteClient;
    private List<String> jobList;
    private List<Failure> failList;
    private Logger log = Logger.getLogger(FailureBuilder.class.getName());

    public FailureBuilder(RemoteClient remoteClient, List<String> jobList) {
        this.remoteClient = remoteClient;
        this.jobList = jobList;
    }

    public List<Failure> readErrors() {
        failList = new ArrayList<Failure>();

        for (String fail : jobList) {
            try {
                String url = remoteClient.getConnectionUrl() + fail + "/" + LAST_STATE + TEST_STATE + JSON_EXTENTION;
                JsonNode jsNode = remoteClient.getJsonNodeFromUrl(url);
                getDataFromJsonFailures(jsNode, fail);
            } catch (IOException exe) {
                log.info("An unexpected error has occurred");
                exe.printStackTrace();
            } catch (JenkinsException jex) {
                log.info(jex.getMessage());
            }
        }
        return failList;
    }

    private void getDataFromJsonFailures(JsonNode jsNode, String job) throws IOException, JenkinsException {
        if (jsNode.has("childReports")) {
            String url = "";
            for (JsonNode node : jsNode.get("childReports")) {
                if (node.has("child")) {
                    url = node.get("child").get("url").asText();
                    String[] urlParts = url.split("/");
                    url = remoteClient.getConnectionUrl() + urlParts[urlParts.length - 3] + "/" + urlParts[urlParts.length - 2]
                        + LAST_STATE + TEST_STATE + JSON_EXTENTION;
                    JsonNode jn = remoteClient.getJsonNodeFromUrl(url);
                    if (jn.has("suites")) {
                        for (JsonNode jsn : jn.get("suites")) {
                            if (jsn.has("cases")) {
                                getStrArray(jsn.get("cases"), job);
                            }
                        }
                    }
                }
            }
        } else if(jsNode.has("suites")){
            for (JsonNode node : jsNode.get("suites")) {
                if (node.has("cases")) {
                    getStrArray(node.get("cases"), job);
                }
            }
        }
    }

    public void getStrArray(JsonNode jsNode, String job) {
        for (JsonNode node : jsNode) {
            if (node.has("status")) {
                if (node.get("status").asText().equals("FAILED") || node.get("status").asText().equals("REGRESSION")) {
                    String[] classname = node.get("className").asText().split("\\.");
                    if ((node.get("errorDetails") == null) || (node.get("errorDetails").asText().equals("null"))) {
                        if (!failList.contains(new Failure(0, classname[classname.length - 1], "", "", "", job))) {
                            failList.add(new Failure(DatatypeConverter.parseInt(node.get("age").asText()),
                                classname[classname.length - 1], "", node.get("errorStackTrace").asText()
                                    .replaceAll("\\n", " "), node.get("status").asText(), job));
                        }
                    } else {
                        if (!failList.contains(new Failure(0, classname[classname.length - 1], "", "", "", job))) {
                            failList.add(new Failure(DatatypeConverter.parseInt(node.get("age").asText()),
                                classname[classname.length - 1], node.get("errorDetails").asText()
                                    .replaceAll("\\n", " "), "", node.get("status").asText(), job));
                        }
                    }
                }
            }
        }
    }
}
