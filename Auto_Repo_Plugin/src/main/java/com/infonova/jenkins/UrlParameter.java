package com.infonova.jenkins;

/**
 * Created by christian.jahrbacher on 30.07.2015.
 */
public interface UrlParameter {

    //TODO: Move this interface into RemoteClient class to hide implemenation

    String JSON_EXTENTION = "/api/json";
    String LAST_STATE = "/lastBuild";
    String STABLE_STATE = "/lastStableBuild";
    String TEST_STATE = "/testReport";
}
