package com.infonova.jenkins;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by dominic.gross on 11.08.2015.
 */
public class Sonar {
    private int lines;
    private int[] ncloc;//number of code lines
    private int[] classes;
    private int[] files;
    private int[] directories;
    private int[] functions;
    private int[] accessors;
    private int[] statements;
    private int[] blocker_violations;
    private int[] critical_violations;
    private int[] major_violations;
    private int[] minor_violations;
    private int[] info_violations;
    private int[] new_violations;
    private int[] open_issues;
    private double[] sqale_index;
    private double[] new_technical_debt;

    public JsonNode content;
    private String url;


    public void setSonar(JsonNode jn)
    {
        this.content = jn;
        for(JsonNode jnode: content)
        {
            System.out.println("geht rein");
            if(jnode.has("msr"))
            {
                System.out.println(true);
            }
//            if(jnode.has("lines"))
//            {
//                lines = jnode.get("val").asInt();
//            }
//            if(jnode.has(""))
        }
    }

    public String getUrl() {
        return url;
    }

//    public int getLines()
//    {
//        content.get("");
//    }
}