package com.eskdr.eskandar;

public class Location {

    private Long id;
    private double latitude;
    private double longitude;

    public Location (Long id, double latitude, double longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
