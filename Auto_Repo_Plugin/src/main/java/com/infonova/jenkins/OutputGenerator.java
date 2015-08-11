package com.infonova.jenkins;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by matthias.augustin on 06.08.15.
 */
public interface OutputGenerator {

    void write(File file, List<JenkinsSystem> jenkinsSystems) throws IOException;
}
