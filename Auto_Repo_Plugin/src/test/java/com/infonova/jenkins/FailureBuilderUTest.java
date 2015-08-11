package com.infonova.jenkins;

/**
 * Created by dominic.gross on 03.08.2015.
 */

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.util.Arrays;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FailureBuilderUTest extends EasyMockSupport {

    private RemoteClient remoteClient;
    private final static String standardUrl = "";
    private FailureBuilder failureBuilder;

    @Before
    public void setup() {
        remoteClient = createMock(RemoteClient.class);
        failureBuilder = new FailureBuilder(remoteClient, Arrays.asList("A1ON-java-build-trunk"));
    }

    @Test
    @Ignore
    public void testFailureBuilder() throws IOException, RemoteException {
        JsonNode jn = new ObjectMapper()
            .readTree("{\"duration\":1058.291,\"empty\":false,\"failCount\":0,\"passCount\":31,\"skipCount\":1,\"suites\":[{\"cases\":[{\"age\":1,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"duration\":54.998,\"errorDetails\":\"Test\",\"errorStackTrace\":\"TestStack\",\"failedSince\":0,\"name\":\"lockAndUnlockUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"FAILED\",\"stderr\":null,\"stdout\":null},{\"age\":1,\"className\":\"com.infonova.jtf.ta.generic\",\"duration\":54.998,\"errorDetails\":null,\"errorStackTrace\":\"TestStack\",\"failedSince\":0,\"name\":\"lockUnlockUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"FAILED\",\"stderr\":null,\"stdout\":null},{\"age\":0,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"duration\":54.998,\"errorDetails\":null,\"errorStackTrace\":null,\"failedSince\":0,\"name\":\"lockAndUser\",\"skipped\":false,\"skippedMessage\":null,\"status\":\"PASSED\",\"stderr\":null,\"stdout\":null}],\"duration\":85.029,\"id\":null,\"name\":\"com.infonova.jtf.ta.generic.kundenverwaltung.CustomerAndServiceLockUnlockTest\",\"stderr\":null,\"stdout\":null,\"timestamp\":null}]}");
        JsonNode jn2 = new ObjectMapper()
            .readTree("{\"failCount\":0,\"skipCount\":1,\"totalCount\":32,\"urlName\":\"testReport\",\"childReports\":[{\"child\":{\"number\":21,\"url\":\"https://ci.infonova.at/job/A1OpenNet/job/A1ON-jtf-livetests-trunk12c/com.bearingpoint.ta$opennet-jtf-tests/21/\"},\"result\":{\"duration\":1058.291,\"empty\":false,\"failCount\":0,\"passCount\":31,\"skipCount\":1,\"suites\":[{\"cases\":[{\"age\":0,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.AonAccountManagementTest\",\"duration\":3.802,\"failedSince\":0,\"name\":\"testGetProductsForAonAccount\",\"skipped\":false,\"status\":\"PASSED\"},{\"age\":0,\"className\":\"com.infonova.jtf.ta.generic.kundenverwaltung.AonAccountManagementTest\",\"duration\":1.302,\"failedSince\":0,\"name\":\"testGetAonAccounts\",\"skipped\":false,\"status\":\"FAILED\"}],\"duration\":5.104,\"id\":null,\"name\":\"com.infonova.jtf.ta.generic.kundenverwaltung.AonAccountManagementTest\",\"timestamp\":null}]}}]}");
        try {
            expect(remoteClient.getConnectionUrl()).andReturn("");
            expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jn2);
            expect(remoteClient.getConnectionUrl()).andReturn("");
            expect(remoteClient.getJsonNodeFromUrl(anyObject(String.class))).andReturn(jn);
        } catch (IOException exe) {
            exe.printStackTrace();
        } catch (RemoteException jex) {
            jex.printStackTrace();
        }
        replayAll();
        failureBuilder.readErrors();
        verifyAll();
    }
}
