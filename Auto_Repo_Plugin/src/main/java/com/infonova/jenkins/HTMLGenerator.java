package com.infonova.jenkins;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by dominic.gross on 30.07.2015.
 */
public class HTMLGenerator implements OutputGenerator{

    private int starthoch = 0;
    private int failhoch =1;

    //TODO: Information Hiding
    @Override
    public void write(File file, List<JenkinsSystem> jenkinsSystems) throws IOException {

    }


    public void staticPreCode(BufferedWriter bwr) throws IOException {


        bwr.write("<!DOCTYPE html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>");
        bwr.newLine();
        bwr.write("<style type=\"text/css\">\n"
                + ".tg  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#ccc;}\n"
                + ".tg td{font-weight:bold;font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}\n"
                + ".tg th{font-family:Arial, sans-serif;font-size:18px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal}\n"
                + ".tg .tgheader{font-weight:bold;font-size:36px;text-align:center}\n"
                +".tg .tg-failtd{border-color:#ccc;background-color:#ff8f8f;font-size:18px;}" +

                "</style>");
        bwr.newLine();
    }

    public void buildTable(List<Job> list, BufferedWriter bwr, String name, List<Failure> failList, String colname)
            throws IOException {

        Color color = Color.decode(colname);

        Color c1 = new Color(color.getRed(),color.getGreen(),color.getBlue(),65);
        Color c2 = new Color(color.getRed(),color.getGreen(),color.getBlue(),25);
        float c1a = c1.getAlpha();
        float c2a = c2.getAlpha();
        System.out.println(c1a);
        int red= (int)((1-(c1a/255))*255)+(int)(c1a/255*c1.getRed());
        int green= (int)((1-(c1a/255))*255)+(int)(c1a/255*c1.getGreen());
        int blue= (int)((1-(c1a/255))*255)+(int)(c1a/255*c1.getBlue());
        System.out.println(red+" "+green+"  "+blue);
        int red2= (int)((1-(c2a/255))*255)+(int)(c2a/255*c2.getRed());
        int green2= (int)((1-(c2a/255))*255)+(int)(c2a/255*c2.getGreen());
        int blue2= (int)((1-(c2a/255))*255)+(int)(c2a/255*c2.getBlue());



        bwr.write("<table class=tg><tr><th class=tgheader style=background-color:rgb("+ color.getRed()+","+color.getGreen()+","+color.getBlue()+") colspan=3>" + name + "</th></tr>");
        bwr.newLine();
        bwr.write("<tr style=background-color:rgb("+ red+","+green+","+blue+")><td class=tg>Step</td><td class=tg>Ergebnis</td><td class=tg>Zuletzt gr&uuml;n</td></tr>");
        bwr.newLine();

        String hilf = name;
        String hoch = "";
        boolean bool = false;
        for (Job r : list) {
            for (Failure f : failList) {
                if (!bool && f.getJobName().equals(r.getJobName()) && !f.getFailure().equals("NoFailure")) {
                    starthoch++;
                    hoch = "" + starthoch;
                    bool = true;
                }
            }
            if (r.getResult().equals("OK") || (r.getResult().contains("NOK"))) {
                name = hilf;
            } else {
                name = "fail";
            }
            bwr.write("<tr style=background-color:rgb("+red2+","+green2+","+blue2+")><td class=tg><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                    + r.getJobName() + ">" + r.getJobName() + "</a></td></a><td class=tg>" + r.getResult()
                    + "<sup>" + hoch + "</sup>" + "</td><td class=tg>" + r.getLastStableDate()
                    + "</td></tr>");
            bwr.newLine();
            hoch = "";
            bool = false;
        }
        bwr.write("</table></br><p>.</p>");
        bwr.newLine();
    }

    public void staticPostCode(BufferedWriter bwr, String sonar,
                               String codecove) throws IOException {
        bwr.write("<p style=font-size:20px><b>Sonar (Trunk) - <a href=" + sonar + "><u>Link</u></a></b></p>");
        bwr.newLine();
        bwr.write("<p style=font-size:20px><b>Code Coverage - <a href=" + codecove + ">Link</a></b></p>");
        bwr.newLine();

    }

    public void buildFailureTable(List<Job> jobList, BufferedWriter bwr, String systemName, List<Failure> failList, String color) throws IOException {

        boolean bool = false;

        for (Job j : jobList) {
            String before = "";
            for (Failure f : failList) {
                int custlength = 160;
                if (f.getJobName().equals(before)) {
                    if (f.getFailure().length() < 160) {
                        custlength = f.getFailure().length();
                    }
                    if (bool && !f.getFailure().equals("NoFailure") && f.getJobName().equals(j.getJobName())) {
                        bwr.write("<tr style=font-size:16px ><td></td><td>" + f.getClassname() + "</b></td><td>"
                                + f.getFailure().substring(0, custlength) + "</td><td>" + f.getAge() + "</td></tr>");
                        bwr.newLine();
                    }
                } else {
                    before = f.getJobName();
                    if (f.getJobName().equals(j.getJobName()) && !f.getFailure().equals("NoFailure")) {
                        bwr.write("<table class=tg><th colspan=4>" + f.getJobName() + "</th></tr>");
                        bwr.newLine();
                        bwr.write("<tr style=font-size:16px><td>Number</td><td>JobName</td><td>Failure</td><td>Age</td></tr>");
                        bwr.newLine();
                    }
                    if (f.getFailure().length() < 160) {
                        custlength = f.getFailure().length();
                    }
                    if (f.getJobName().equals(j.getJobName()) && !f.getFailure().equals("NoFailure")) {
                        bwr.write("<tr style=font-size:16px><td><b>" + failhoch + "</td><td>" + f.getClassname()
                                + " </b></td><td> " + f.getFailure().substring(0, custlength) + "</td><td>" + f.getAge()
                                + "</td></tr>");
                        bwr.newLine();
                        bool = true;
                    }
                }
            }
            bwr.newLine();
            if (bool) {
                failhoch++;
                bwr.write("</table>");
                bwr.newLine();
            }
            bool = false;
            bwr.newLine();
        }

    }

}
