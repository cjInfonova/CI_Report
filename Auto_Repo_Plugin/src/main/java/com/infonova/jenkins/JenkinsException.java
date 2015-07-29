package com.infonova.jenkins;

/**
 * Created by christian.jahrbacher on 23.07.2015.
 */
public class JenkinsException extends Exception {
    public JenkinsException(){}

    public JenkinsException(String message){
        super(message);
    }
}
