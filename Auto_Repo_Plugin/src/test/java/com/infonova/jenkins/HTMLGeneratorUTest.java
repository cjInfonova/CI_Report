package com.infonova.jenkins;

/**
 * Created by dominic.gross on 31.07.2015.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.infonova.jenkins.Failure;
import com.infonova.jenkins.HTMLGenerator;
import com.infonova.jenkins.Job;


public class HTMLGeneratorUTest extends EasyMockSupport {

    public HTMLGenerator htmlgen;
    public final static List<Job> jobList = Arrays.asList(new Job("Test1", "FAILED", 1, 2, "no"), new Job("Test2", "PASSED", 0, 2, "no"), new Job("Test2", "SKIPPED", 0, 2, "no"));
    public final static List<Failure> failList = Arrays.asList(new Failure(1, "class1", "fail all", "ff", "FAILED", "Test1"), new Failure(0, "class2", "", "", "PASSED", "Test2"), new Failure(0, "class3", "", "", "PASSED", "Test2"));


    @Before
    public void Setup() {
        htmlgen = new HTMLGenerator();
    }

    @Test @Ignore
    public void testPreCode() throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("test.html")));

        replayAll();
        htmlgen.staticPreCode(bwr);
        verifyAll();

    }

    @Test @Ignore
    public void testBuildTable() throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("test.html")));

        replayAll();
        htmlgen.buildTable(jobList, bwr, "Tab1", failList, "#ffffff");
        verifyAll();

    }


    @Test @Ignore
    public void testPostCode() throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("test.html")));

        replayAll();
        htmlgen.staticPostCode(bwr, "link1", "link2");
        verifyAll();
    }
}
