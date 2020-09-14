package com.example.googlemap_navigation;

import com.skt.Tmap.TMapMarkerItem;

public class Marker {
    private String name;
    private String address;
    private String marker_id;
    private double latitude;
    private double longitude;


    public Marker(){
        name = "";
        address = "";
        marker_id = "";
        latitude = 0;
        longitude = 0;
    }

    public Marker(String _name, String _address, String _id, double _latitude, double _longitude){
        name = _name;
        address = _address;
        marker_id = _id;
        latitude = _latitude;
        longitude = _longitude;
    }

    public void setMarker_id(String marker_id) {
        this.marker_id = marker_id;
    }

    public String getMarker_id() {
        return marker_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
