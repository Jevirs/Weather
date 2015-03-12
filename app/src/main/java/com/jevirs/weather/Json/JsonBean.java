package com.jevirs.weather.Json;

import java.util.List;

/**
 * Created by jevirs on 2015/3/6.
 */
public class JsonBean {
    private String error;
    private String status;
    private String date;
    private List<Results> results;

    public String getError(){
        return error;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public List<Results> getResults() {
        return results;
    }
}