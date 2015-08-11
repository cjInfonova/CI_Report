package com.infonova.jenkins;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by dominic.gross on 11.08.2015.
 */
public class Sonar {
    private int lines;
    private int lines_ch;
    private int ncloc;//number of code lines
    private int ncloc_ch;
    private int classes;
    private int classes_ch;
    private int files_ch;
    private int directories_ch;
    private int functions_ch;
    private int accessors_ch;
    private int statements_ch;
    private int blocker_violations_ch;
    private int critical_violations_ch;
    private int major_violations_ch;
    private int minor_violations_ch;
    private int info_violations_ch;
    private int new_violations_ch;
    private int open_issues_ch;
    private double sqale_index_ch;
    private double new_technical_debt_ch;
    private int files;
    private int directories;
    private int functions;
    private int accessors;
    private int statements;
    private int blocker_violations;
    private int critical_violations;
    private int major_violations;
    private int minor_violations;
    private int info_violations;
    private int new_violations;
    private int open_issues;
    private double sqale_index;
    private double new_technical_debt;

    public JsonNode content;
    private String url;
    private String createdAfter;

    public Sonar()
    {

    }

    public void setSonar(JsonNode jn, String period)
    {

        this.content = jn;
        String var = "p"+period+"d";
        for(JsonNode jnode: content)
        {
            createdAfter = jnode.get(var).asText();
            if(jnode.has("msr"))
            {
                for (JsonNode jnode2: jnode)
                {

                    if(jnode2.has("lines"))lines = jnode2.get("val").asInt();
                    if(jnode2.has("ncloc"))ncloc = jnode2.get("val").asInt();
                    if(jnode2.has("classes")) classes= jnode2.get("val").asInt();
                    if(jnode2.has("files")) files= jnode2.get("val").asInt();
                    if(jnode2.has("directories")) directories= jnode2.get("val").asInt();
                    if(jnode2.has("functions"))functions = jnode2.get("val").asInt();
                    if(jnode2.has("accessors")) accessors= jnode2.get("val").asInt();
                    if(jnode2.has("statements")) statements = jnode2.get(var).asInt();
                    if(jnode2.has("blocker_violations"))blocker_violations = jnode2.get("val").asInt();
                    if(jnode2.has("critical_violations"))critical_violations = jnode2.get("val").asInt();
                    if(jnode2.has("major_violations")) major_violations= jnode2.get("val").asInt();
                    if(jnode2.has("minor_violations")) minor_violations= jnode2.get("val").asInt();
                    if(jnode2.has("info_violations")) info_violations= jnode2.get("val").asInt();
                    if(jnode2.has("new_violations")) new_violations= jnode2.get("val").asInt();
                    if(jnode2.has("open_issues")) open_issues= jnode2.get("val").asInt();
                    if(jnode2.has("sqale_index")) sqale_index= jnode2.get("val").asDouble();
                    if(jnode2.has("new_technical_debt")) new_technical_debt= jnode2.get("val").asDouble();


                    if(jnode2.has("lines"))lines_ch = jnode2.get(var).asInt();
                    if(jnode2.has("ncloc"))ncloc_ch = jnode2.get(var).asInt();
                    if(jnode2.has("classes")) classes_ch= jnode2.get(var).asInt();
                    if(jnode2.has("files")) files_ch= jnode2.get(var).asInt();
                    if(jnode2.has("directories")) directories_ch= jnode2.get("val").asInt();
                    if(jnode2.has("functions"))functions_ch = jnode2.get(var).asInt();
                    if(jnode2.has("accessors")) accessors_ch= jnode2.get(var).asInt();
                    if(jnode2.has("statements")) statements_ch = jnode2.get(var).asInt();
                    if(jnode2.has("blocker_violations"))blocker_violations_ch = jnode2.get(var).asInt();
                    if(jnode2.has("critical_violations"))critical_violations_ch = jnode2.get(var).asInt();
                    if(jnode2.has("major_violations")) major_violations_ch= jnode2.get(var).asInt();
                    if(jnode2.has("minor_violations")) minor_violations_ch= jnode2.get(var).asInt();
                    if(jnode2.has("info_violations")) info_violations_ch= jnode2.get(var).asInt();
                    if(jnode2.has("new_violations")) new_violations_ch= jnode2.get(var).asInt();
                    if(jnode2.has("open_issues")) open_issues_ch= jnode2.get(var).asInt();
                    if(jnode2.has("sqale_index")) sqale_index_ch= jnode2.get(var).asDouble();
                    if(jnode2.has("new_technical_debt")) new_technical_debt_ch= jnode2.get(var).asDouble();


                }

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

    public String getCreatedAfter() {
        return createdAfter;
    }

//    public int getLines()
//    {
//        content.get("");
//    }
}