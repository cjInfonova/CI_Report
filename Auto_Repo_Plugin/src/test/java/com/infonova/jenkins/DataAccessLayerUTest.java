package com.infonova.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;

/**
 * Created by dominic.gross on 21.07.2015.
 */
public class DataAccessLayerUTest extends EasyMockSupport {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final static String JOB_NAME = "A1OpenNet";
    private final static String STANDARD_URL = "https://ci.infonova.at/";

    private DataAccessLayer dal;

    private JenkinsAccess jenkinsAccess;
    private HTMLGenerator htmlgen;
    private JobBuilder jobBuilder;
    private List<JenkinsSystem> jenkinsSystemList;

    @Before
    public void setup(){
        jenkinsAccess=createMock(JenkinsAccess.class);
        htmlgen = createMock(HTMLGenerator.class);
        jobBuilder = createMock(JobBuilder.class);
        jenkinsSystemList = createMock(ArrayList.class);
        dal = new DataAccessLayer(jenkinsAccess,STANDARD_URL,JOB_NAME,simpleDateFormat,jobBuilder,htmlgen,jenkinsSystemList);
    }



    public void testStartBuidlingReportEMPTY() throws IOException, JenkinsException {
        expect(jobBuilder.prepareEverything(anyObject(ArrayList.class))).andReturn(null);

        replayAll();
        dal.startBuildingReport();
        verifyAll();
    }


    public void testStartBuidlingReportFULL() throws IOException, JenkinsException {
        List<Job> jobList = Arrays.asList(new Job("A1ON-java-build-trunk","FAILED",2,3,""));

        expect(jobBuilder.prepareEverything(anyObject(ArrayList.class))).andReturn(jobList);

        boolean bool=false;
        String url ;
        JsonNode jn = new ObjectMapper().readTree("{\"duration\":1058.291,\"empty\":false,\"failCount\":0,\"passCount\":31,\"skipCount\":1,\"suites\":[{\"cases\":[{\"age\":1,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"duration\":54.998,\"errorDetails\":\"Test\",\"errorStackTrace\":\"TestStack\",\"failedSince\":0,\"name\":\"lockAndUnlockUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"FAILED\",\"stderr\":null,\"stdout\":null}],\"duration\":85.029,\"id\":null,\"name\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"stderr\":null,\"stdout\":null,\"timestamp\":null}]}");

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jn).anyTimes();
        BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("dataTest.html")));
        htmlgen.staticPreCode(anyObject(BufferedWriter.class));
        htmlgen.buildTable(anyObject(ArrayList.class), anyObject(BufferedWriter.class), anyObject(String.class), anyObject(ArrayList.class));
        expectLastCall().anyTimes();
        htmlgen.staticPostCode(anyObject(BufferedWriter.class), anyObject(String.class),anyObject(String.class));

        replayAll();
        dal.startBuildingReport();
        verifyAll();
    }



}
