package com.infonova.jenkins;

/**
 * Created by dominic.gross on 27.07.2015.
 */
public class Failure {

    private int age;
    private String jobname;
    private String className;
    private String errDetails;
    private String errStack;
    private String status;

    public Failure(int age, String className, String errDetails, String errStack, String status, String jobname) {
        this.age = age;
        this.errDetails = errDetails;
        this.errStack = errStack;
        this.status = status;
        this.className = className;
        this.jobname = jobname;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Failure) {
            Failure f = (Failure)o;
            if (this.jobname.equals(f.jobname) && this.className.equals(f.className)) {
                return true;
            }
        }
        return false;
    }

    public String getFailure() {
        return errDetails + errStack;
    }

    public String getJobName() {
        return jobname;
    }

    public String getClassname() {
        return className;
    }

    public int getAge() {
        return age;
    }
}
