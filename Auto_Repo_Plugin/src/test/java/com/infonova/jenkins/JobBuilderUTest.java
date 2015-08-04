package com.infonova.jenkins;

import netscape.javascript.JSException;
import org.easymock.EasyMockSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infonova.jenkins.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.not;

/**
 * Created by christian.jahrbacher on 03.08.2015.
 */
public class JobBuilderUTest extends EasyMockSupport {

    private final static String STANDARD_URL = "https://ci.infonova.at/job/A1OpenNet/job/";
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private JobBuilder jobBuilder;

    private JenkinsAccess jenkinsAccess;
    private List<String> jobList;
    private JsonNode jsNode;

    @Before
    public void setup() {
        jenkinsAccess = createMock(JenkinsAccess.class);
        jobList = Arrays.asList("A1ON-java-build-trunk");

        jobBuilder = new JobBuilder(jenkinsAccess, STANDARD_URL, dateFormat);
    }

    @Test
    public void prepareEverythingWithResultSuccess() throws IOException, JenkinsException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(false, "\"SUCCESS\"");

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode);
        assert(job.getResultString().equals("SUCCESS"));

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test
    public void prepareEverythingWithResultAborted() throws IOException, JenkinsException {
        Job job = new Job("MyTestJob", "ABORTED", 0, 10, "-");
        createJsonNode(false, "\"ABORTED\"");

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode);
        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode);
        assert(!job.getResultString().equals("SUCCESS"));

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test
    public void prepareEverythingWithBuildingTrue() throws IOException, JenkinsException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(true, null);

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode);
        assert(jsNode.get("building").asBoolean());

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test
    public void prepareEverythingWithWrongSource() throws IOException, JenkinsException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(true, null);

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andThrow(new JenkinsException("Source not found"));

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test
    public void prepareEverythingWithOtherJenkinsException() throws IOException, JenkinsException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(true, null);

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andThrow(new JenkinsException("Another Error has occurred"));


        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test
    public void prepareEverythingWithIOException() throws IOException, JenkinsException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(true, null);

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andThrow(new IOException());

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    private void createJsonNode(boolean building, String result) throws IOException, JSException{
        jsNode = new ObjectMapper().readTree("{\"actions\":[{\"causes\":[{\"shortDescription\":\"Build wurde durch eine SCM-Änderung ausgelöst.\"}]},{},{\"failCount\":0,\"skipCount\":7,\"totalCount\":2633,\"urlName\":\"testReport\"}],\"building\":"+building+",\"description\":null,\"fullDisplayName\":\"A1OpenNet » A1ON-java-build-trunk #5710\",\"id\":\"2015-08-03_16-12-39\",\"result\":"+result+",\"timestamp\":1438611159844}");
    }
}
