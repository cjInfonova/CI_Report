package com.infonova.jenkins;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by christian.jahrbacher on 03.08.2015.
 */
public class JobBuilderUTest extends EasyMockSupport {

    private final static String STANDARD_URL = "https://ci.infonova.at/job/A1OpenNet/job/";
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private JobBuilder jobBuilder;

    private RemoteClient remoteClient;
    private List<String> jobList;
    private JsonNode jsNode;

    @Before
    public void setup() {
        remoteClient = createMock(RemoteClient.class);
        jobList = Arrays.asList("A1ON-java-build-trunk");

        jobBuilder = new JobBuilder(remoteClient, dateFormat);
    }

    @Test @Ignore
    public void prepareEverythingWithResultSuccess() throws IOException, RemoteException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(false, "\"SUCCESS\"");

        expect(remoteClient.getConnectionUrl()).andReturn("");
        expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode);
        assert(job.getResultString().equals("SUCCESS"));

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test @Ignore
    public void prepareEverythingWithResultAborted() throws IOException, RemoteException {
        Job job = new Job("MyTestJob", "ABORTED", 0, 10, "-");
        createJsonNode(false, "\"ABORTED\"");

        expect(remoteClient.getConnectionUrl()).andReturn("").anyTimes();
        expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode).anyTimes();
        assert(!job.getResultString().equals("SUCCESS"));

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test @Ignore
    public void prepareEverythingWithBuildingTrue() throws IOException, RemoteException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(true, null);

        expect(remoteClient.getConnectionUrl()).andReturn("");
        expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jsNode);
        assert(jsNode.get("building").asBoolean());

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test @Ignore
    public void prepareEverythingWithWrongSource() {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        try {
            createJsonNode(true, null);
            expect(remoteClient.getConnectionUrl()).andReturn("");
            expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andThrow(new RemoteException("Source not found"));
        }catch (IOException iex){}
        catch (RemoteException jex){}
        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test @Ignore
    public void prepareEverythingWithOtherJenkinsException() throws IOException, RemoteException {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        createJsonNode(true, null);

        expect(remoteClient.getConnectionUrl()).andReturn("");
        expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andThrow(new RemoteException("Another Error has occurred"));

        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    @Test @Ignore
    public void prepareEverythingWithIOException() {
        Job job = new Job("MyTestJob", "SUCCESS", 0, 10, "-");
        try {
            createJsonNode(true, null);
            expect(remoteClient.getConnectionUrl()).andReturn("");
            expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andThrow(new IOException());
        }catch (IOException iex){}
        catch (RemoteException jex){}
        replayAll();
        jobBuilder.prepareEverything(jobList);
        verifyAll();
    }

    private void createJsonNode(boolean building, String result) throws IOException {
        jsNode = new ObjectMapper().readTree("{\"actions\":[{\"causes\":[{\"shortDescription\":\"Build wurde durch eine SCM-Änderung ausgelöst.\"}]},{},{\"failCount\":0,\"skipCount\":7,\"totalCount\":2633,\"urlName\":\"testReport\"}],\"building\":"+building+",\"description\":null,\"fullDisplayName\":\"A1OpenNet » A1ON-java-build-trunk #5710\",\"id\":\"2015-08-03_16-12-39\",\"result\":"+result+",\"timestamp\":1438611159844}");
    }
}
