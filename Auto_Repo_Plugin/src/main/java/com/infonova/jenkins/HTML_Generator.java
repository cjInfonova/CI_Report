package com.infonova.jenkins;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by dominic.gross on 30.07.2015.
 */
public class HTML_Generator {

    private int starthoch = 0;

    protected void staticPreCode(BufferedWriter bwr) throws IOException {
        bwr.write("<!DOCTYPE html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>");
        bwr.newLine();
        bwr.write("<style type=\"text/css\">\n"
            + ".tg  {width:100%;border-collapse:collapse;border-spacing:0;border-color:#ccc;}\n"
            + ".tg td{font-weight:bold;font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}\n"
            + ".tg th{font-family:Arial, sans-serif;font-size:18px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal}\n"
            + ".tg .tg-TRUNKh{font-weight:bold;font-size:36px;background-color:#4875cc;text-align:center}\n"
            + ".tg .tg-TRUNKth{background-color:#bfd2f7;font-size:18px;border-color:#aabcfe}\n"
            + ".tg .tg-TRUNKtd{border-color:#aabcfe;background-color:#e8edff;}\n" +

            ".tg .tg-RCh{font-weight:bold;font-size:36px;background-color:#f56b00}\n"
            + ".tg .tg-RCth{background-color:#FCFBE3;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-RCtd{background-color:#fff;border-color:#ccc;}" +

            ".tg .tg-BFh{font-weight:bold;font-size:36px;background-color:#0aba0b}\n"
            + ".tg .tg-BFth{background-color:#C2FFD6;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-BFtd{border-color:#bbb;background-color:#E0FFEB;}" +

            ".tg .tg-UAT4h{font-weight:bold;font-size:36px;background-color:#c0c0c0}\n"
            + ".tg .tg-UAT4th{background-color:#efefef;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-UAT4td{border-color:#ccc;background-color:#fff;}" +

            ".tg .tg-RC2h{font-weight:bold;font-size:36px;background-color:#674421}\n"
            + ".tg .tg-RC2th{background-color:#bd9c7b;font-size:18px;border-color:#ccc}\n"
            + ".tg .tg-RC2td{border-color:#ccc;background-color:#f7ece0;}\n" +

            ".tg .tg-failtd{border-color:#ccc;background-color:#ff8f8f;font-size:18px;}" +

            "</style>");
        bwr.newLine();
    }

    protected void buildTable(List<Job> list, BufferedWriter bwr, String name, List<Failure> failList)
            throws IOException {
        bwr.write("<table class=tg><tr><th class=tg-" + name + "h colspan=" + 3 + ">" + name + "</th></tr>");
        bwr.newLine();
        bwr.write("<tr><td class=tg-" + name + "th>Step</td><td class=tg-" + name + "th>Ergebnis</td><td class=tg-"
            + name + "th>Zuletzt gr&uuml;n</td></tr>");
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
            bwr.write("<tr><td class=tg-" + name + "td><a href=https://ci.infonova.at/job/A1OpenNet/job/"
                + r.getJobName() + ">" + r.getJobName() + "</a></td></a><td class=tg-" + name + "td>" + r.getResult()
                + "<sup>" + hoch + "</sup>" + "</td><td class=tg-" + name + "td>" + r.getLastStableDate()
                + "</td></tr>");
            bwr.newLine();
            hoch = "";
            bool = false;
        }
        bwr.write("</table></br><p>.</p>");
        bwr.newLine();
    }

    protected void staticPostCode(BufferedWriter bwr) throws IOException {
        bwr.write("<p style=font-size:20px><b>Sonar (Trunk) - <a href=" + sonar + "><u>Link</u></a></b></p>");
        bwr.newLine();
        bwr.write("<p style=font-size:20px><b>Code Coverage - <a href=" + codecove + ">Link</a></b></p>");
        bwr.newLine();
        int i = 1;
        boolean bool = false;

        for (Job j : jobClassList) {
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
                        bwr.write("<tr style=font-size:16px><td><b>" + i + "</td><td>" + f.getClassname()
                            + " </b></td><td> " + f.getFailure().substring(0, custlength) + "</td><td>" + f.getAge()
                            + "</td></tr>");
                        bwr.newLine();
                        bool = true;
                    }
                }
            }
            bwr.newLine();
            if (bool) {
                i++;
                bwr.write("</table>");
                bwr.newLine();
            }
            bool = false;
            bwr.newLine();
        }
        bwr.write("</html>");
    }
}
