package com.infonoca.jenkins;

//import com.bearingpoint.jbpm.TestExecutionContext;
import com.infonova.easymock.EasyMockRule;
import com.infonova.easymock.annotation.Mock;
import com.infonova.easymock.annotation.UnitToTest;
import com.infonova.jenkins.DataAccessLayer;
import org.apache.maven.plugins.annotations.Mojo;
import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Date;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

/**
 * Created by dominic.gross on 21.07.2015.
 */

public class ReportMojoUTest {

    private static final DataAccessLayer dal = new DataAccessLayer();
    @Test
    public void execute() {
        dal.setupJobList();

        dal.setupJobList();

        dal.getAllJsonsFromJenkins();

        dal.generateHTML();

    }

}
