package com.jevirs.weather.Json;

/**
 * Created by jevirs on 2015/3/6.
 */
public class Weather_data {
    private String date;
    private String dayPictureUrl;
    private String nightPictureUrl;
    private String weather;
    private String wind;
    private String temperature;

    public String getDate(){
        return  date;
    }

    public String getDayPictureUrl(){
        return dayPictureUrl;
    }

    public String getNightPictureUrl(){
        return nightPictureUrl;
    }

    public String getWeather(){
        return weather;
    }

    public String getWind(){
        return wind;
    }

    public String getTemperature(){
        return temperature;
    }
}