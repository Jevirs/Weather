package com.jevirs.weather.Json;

import java.util.List;

/**
 * Created by jevirs on 2015/3/6.
 */
public class Results {
    private String currentCity;
    private String pm25;
    private List<Index> index;
    private List<Weather_data> weather_data;

    public String getCurrentCity(){
        return currentCity;
    }

    public String getPm25(){
        return pm25;
    }

    public List<Index> getIndex() {
        return index;
    }

    public List<Weather_data> getWeatherData() {
        return weather_data;
    }

}