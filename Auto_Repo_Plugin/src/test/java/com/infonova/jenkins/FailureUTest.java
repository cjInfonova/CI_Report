package com.infonova.jenkins;

/**
 * Created by dominic.gross on 03.08.2015.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infonova.jenkins.*;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;


public class FailureUTest extends EasyMockSupport {

    private final static int age=1;
    private final static String jobname= "JTFPlSqlTestCase";
    private final static String className="A1ON-jtf-db-guidelines-trunk12c";
    private final static String errDetails="Fehler";
    private final static String errStack="Fehler";
    private final static String status="FAILED";

    @Test
    public void testEquals()
    {
        Failure f = new Failure(1,"A1ON-jtf-db-guidelines-trunk12c","","","F","JTFPlSqlTestCase");
        Failure g ;
        replayAll();
        f.equals(new Failure(1, className, "", "", "F", jobname));
        verifyAll();
    }

    @Test
    public void testAllGet()
    {
        Failure f = new Failure(1,"A1ON-jtf-db-guidelines-trunk12c","","","F","JTFPlSqlTestCase");
        replayAll();
        f.getAge();
        f.getJobName();
        f.getFailure();
        f.getClassname();
        verifyAll();

    }
}
