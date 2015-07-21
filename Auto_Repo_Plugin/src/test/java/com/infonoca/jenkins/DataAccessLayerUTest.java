package com.infonoca.jenkins;

import com.infonova.easymock.EasyMockRule;
import com.infonova.easymock.annotation.UnitToTest;
import com.infonova.jenkins.DataAccessLayer;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by dominic.gross on 21.07.2015.
 */
public class DataAccessLayerUTest {

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);
    @UnitToTest
    private DataAccessLayer dal;

    @Test
    public void connectToJenkinsWithWrongUsersettings() {
        dal = new DataAccessLayer();

        dal.setupJobList();
        dal.getAllReportsFromJenkins();
    }
}
