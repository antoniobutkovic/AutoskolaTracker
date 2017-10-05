package com.example.toni.myapplication;

import java.util.List;

/**
 * Created by Toni on 24.9.2017..
 */

public class Students {



    private String rideNum;
    private String fullName;
    private String notes;
    private String date;
    private List<String> lat;
    private List<String> lng;


    public Students() {
    }

    public Students(String fullName, String notes, String date, List<String> lat, List<String> lng, String rideNum) {
        this.fullName = fullName;
        this.notes = notes;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.rideNum = rideNum;

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getLat() {
        return lat;
    }

    public void setLat(List<String> lat) {
        this.lat = lat;
    }

    public List<String> getLng() {
        return lng;
    }

    public void setLng(List<String> lng) {
        this.lng = lng;
    }

    public String getRideNum() {
        return rideNum;
    }

    public void setRideNum(String rideNum) {
        this.rideNum = rideNum;
    }
}
