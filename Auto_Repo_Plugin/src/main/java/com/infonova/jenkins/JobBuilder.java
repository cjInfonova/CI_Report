package com.infonova.jenkins;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by christian.jahrbacher on 30.07.2015.
 */
public class JobBuilder implements UrlParameter {

    private RemoteClient remoteClient;
    private SimpleDateFormat dateFormat;
    private Logger log = Logger.getLogger("MyLogger");

    public JobBuilder(RemoteClient jenAccess, SimpleDateFormat sdf) {
        this.remoteClient = jenAccess;
        this.dateFormat = sdf;
    }

    public List<Job> prepareEverything(List<String> jobList) {
        List<Job> jobClassList = new ArrayList<Job>();
        try {
            for (String jobString : jobList) {
                try {
                    String url = remoteClient.getConnectionUrl() + jobString;
                    JsonNode jsNode = remoteClient.getJsonNodeFromUrl(url + LAST_STATE + JSON_EXTENTION);
                    Job job = convertJsonNodeIntoJob(jsNode, url+STABLE_STATE+JSON_EXTENTION);
                    job.setJobName(jobString);
                    jobClassList.add(job);
                } catch (RemoteException jex) {
                    if (jex.getMessage().contains("Source not found")) {
                        log.info(jex.getMessage() + ": " + jobString);
                    } else {
                        throw jex;
                    }
                } catch (IOException exe) {
                    log.info("An unexpected error has occurred");
                    exe.printStackTrace();
                    exe.printStackTrace();
                } catch (Throwable exe) {
                    log.info(exe.getMessage());
                    exe.printStackTrace();
                }
            }
            log.info("Reports konnten geladen werden: " + jobClassList.size() + "/" + jobList.size());
            return jobClassList;
        } catch (RemoteException jex) {
            log.info(jex.getMessage());
        }
        return null;
    }

    private Job convertJsonNodeIntoJob(JsonNode jsNode, String lastStableUrl) throws RemoteException, IOException {
        Job job = new Job();
        if (jsNode.get("building").asBoolean()) {
            job = new Job("-", "RUNNING", 0, 0, "-");
            return job;
        }
        job.setResult(jsNode.get("result").asText());
        if (!job.getResultString().equals("SUCCESS")) {
            JsonNode node = remoteClient.getJsonNodeFromUrl(lastStableUrl);
            job.setLastStableDate(dateFormat.format(getLastStableDateFromJsonNode(node)));
        } else {
            job.setLastStableDate(dateFormat.format(getLastStableDateFromJsonNode(jsNode)));
        }
        if (jsNode.get("actions").isArray()) {
            for (JsonNode jn : jsNode.get("actions")) {
                if (!jn.isArray() && jn.has("failCount")) {
                    job.setFailCount(jn.get("failCount").asInt());
                    job.setTotalCount(jn.get("totalCount").asInt());
                }
            }
        }
        return job;
    }

    private Date getLastStableDateFromJsonNode(JsonNode jsNode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(jsNode.get("id").asText());
        } catch (ParseException pex) {
            log.info("Can't parse date: " + jsNode.get("id").asText());
        }
        return date;
    }
}
