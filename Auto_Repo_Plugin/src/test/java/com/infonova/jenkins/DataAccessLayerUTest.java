package com.infonova.jenkins;

import static org.easymock.EasyMock.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infonova.jenkins.*;

/**
 * Created by dominic.gross on 21.07.2015.
 */
public class DataAccessLayerUTest extends EasyMockSupport {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final static String JOB_NAME = "A1OpenNet";
    private final static String STANDARD_URL = "https://ci.infonova.at/";

    private DataAccessLayer dal;

    private JenkinsClient jenkinsClient;
    private HTMLGenerator htmlgen;
    private JobBuilder jobBuilder;
    private List<JenkinsSystem> jenkinsSystemList;

    @Before
    public void setup() {
        jenkinsClient = createMock(JenkinsClient.class);
        htmlgen = createMock(HTMLGenerator.class);
        jobBuilder = createMock(JobBuilder.class);
        jenkinsSystemList = new ArrayList<JenkinsSystem>(); // createMock(anyObject(ArrayList.class));
        createJenkinsSysList();
        dal = new DataAccessLayer(jenkinsClient, simpleDateFormat, jobBuilder, htmlgen,
            jenkinsSystemList);
    }

    private void createJenkinsSysList() {
        String colors ="#ffffff";
        List<String> jobStrings = new ArrayList<String>();
        jobStrings.add("TestJob1");
        jobStrings.add("TestJob2");
        jobStrings.add("TestJob3");
        jenkinsSystemList.add(new JenkinsSystem("Test1", colors, jobStrings));
        jenkinsSystemList.add(new JenkinsSystem("Test2", colors, jobStrings));
        jenkinsSystemList.add(new JenkinsSystem("Test3", colors, jobStrings));
    }

    @Test
    public void testStartBuidlingReportEMPTY() throws IOException, JenkinsException {
        for (JenkinsSystem js : jenkinsSystemList) {
            expect(jobBuilder.prepareEverything(js.getJobNameList())).andReturn(null);
        }

        htmlgen.staticPreCode(anyObject(BufferedWriter.class));
        htmlgen.buildTable(anyObject(ArrayList.class), anyObject(BufferedWriter.class), anyObject(String.class),
            anyObject(ArrayList.class), anyObject(String.class));
        expectLastCall().anyTimes();
        htmlgen.staticPostCode(anyObject(BufferedWriter.class), anyObject(String.class), anyObject(String.class));

        for (JenkinsSystem js : jenkinsSystemList) {
            htmlgen.buildFailureTable(anyObject(ArrayList.class), anyObject(BufferedWriter.class),
                anyObject(String.class), anyObject(ArrayList.class), anyObject(String.class));
        }

        replayAll();
        dal.startBuildingReport();
        verifyAll();
    }

    @Test
    public void testStartBuidlingReportFULL() throws IOException, JenkinsException {
        List<Job> jobList = Arrays.asList(new Job("A1ON-java-build-trunk", "FAILED", 2, 3, ""));

        for (JenkinsSystem js : jenkinsSystemList) {
            expect(jobBuilder.prepareEverything(anyObject(ArrayList.class))).andReturn(jobList);
        }

        boolean bool = false;
        String url;
        JsonNode jn = new ObjectMapper()
            .readTree("{\"duration\":1058.291,\"empty\":false,\"failCount\":0,\"passCount\":31,\"skipCount\":1,\"suites\":[{\"cases\":[{\"age\":1,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"duration\":54.998,\"errorDetails\":\"Test\",\"errorStackTrace\":\"TestStack\",\"failedSince\":0,\"name\":\"lockAndUnlockUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"FAILED\",\"stderr\":null,\"stdout\":null}],\"duration\":85.029,\"id\":null,\"name\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"stderr\":null,\"stdout\":null,\"timestamp\":null}]}");

        expect(jenkinsClient.getConnectionUrl()).andReturn("").anyTimes();
        expect(jenkinsClient.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jn).anyTimes();
        BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("dataTest.html")));
        htmlgen.staticPreCode(anyObject(BufferedWriter.class));
        htmlgen.buildTable(anyObject(ArrayList.class), anyObject(BufferedWriter.class), anyObject(String.class),
            anyObject(ArrayList.class), anyObject(String.class));
        expectLastCall().anyTimes();
        htmlgen.staticPostCode(anyObject(BufferedWriter.class), anyObject(String.class), anyObject(String.class));

        for (JenkinsSystem js : jenkinsSystemList) {
            htmlgen.buildFailureTable(anyObject(ArrayList.class), anyObject(BufferedWriter.class),
                anyObject(String.class), anyObject(ArrayList.class), anyObject(String.class));
        }

        replayAll();
        dal.startBuildingReport();
        verifyAll();
    }

}
