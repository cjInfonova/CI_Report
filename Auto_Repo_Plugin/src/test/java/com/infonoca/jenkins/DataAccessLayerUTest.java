package com.infonoca.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infonova.jenkins.*;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

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

    @Before
    public void setup(){
        jenkinsAccess=createMock(JenkinsAccess.class);
        htmlgen = createMock(HTMLGenerator.class);
        jobBuilder = createMock(JobBuilder.class);

        dal = new DataAccessLayer(jenkinsAccess,STANDARD_URL,JOB_NAME,simpleDateFormat,jobBuilder,htmlgen);
    }


    @Test
    public void generateHTML() throws IOException, JenkinsException {
        expect(jobBuilder.prepareEverything(anyObject(ArrayList.class))).andReturn(null);

        replayAll();
        dal.startBuildingReport();
        verifyAll();
    }

    @Test
    public void generateHTML1() throws IOException, JenkinsException {
        List<Job> jobList = new ArrayList<Job>();


        expect(jobBuilder.prepareEverything(anyObject(ArrayList.class))).andReturn(jobList);

        boolean bool=false;
        String url ;
        JsonNode jn = new ObjectMapper().readTree("{\"duration\":1058.291,\"empty\":false,\"failCount\":0,\"passCount\":31,\"skipCount\":1,\"suites\":[{\"cases\":[{\"age\":1,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"duration\":54.998,\"errorDetails\":\"Test\",\"errorStackTrace\":\"TestStack\",\"failedSince\":0,\"name\":\"lockAndUnlockUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"FAILED\",\"stderr\":null,\"stdout\":null}],\"duration\":85.029,\"id\":null,\"name\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"stderr\":null,\"stdout\":null,\"timestamp\":null}]}");

        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jn);

//        expect(jenkinsAccess.getJsonNodeFromUrl(anyObject(String.class))).andReturn(new ObjectMapper().readTree("{\"duration\":1058.291,\"empty\":false,\"failCount\":0,\"passCount\":31,\"skipCount\":1,\"suites\":[{\"cases\":[{\"age\":1,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"duration\":54.998,\"errorDetails\":\"Test\",\"errorStackTrace\":\"TestStack\",\"failedSince\":0,\"name\":\"lockAndUnlockUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"FAILED\",\"stderr\":null,\"stdout\":null}],\"duration\":85.029,\"id\":null,\"name\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"stderr\":null,\"stdout\":null,\"timestamp\":null}]}"));

        replayAll();
        dal.startBuildingReport();
        verifyAll();
    }

}
