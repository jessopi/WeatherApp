package com.weatherapp.controller;

import geocode.consume.GeocodeResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/*
    Class that consumes json from google api and returns location name and coordinates.
 */
@Component
public class Location {
    private RestTemplate restTemplate;
    private String locationString;
    private GeocodeResponse location;

    public Location(){
        restTemplate = new RestTemplate();
    }
    public boolean setAddressInput(String inputLocation){
        this.locationString = inputLocation;
        if(this.locationString != null && this.locationString.trim().length() != 0){
            location = restTemplate.getForObject("https://maps.googleapis.com/maps/api/geocode/json?address="+this.locationString+"&key=AIzaSyAUpsmOT1CxyhSZ9EN_DXaIsXiA-dBBqlM",GeocodeResponse.class);
            if(location.status.equals("OK")){
                return true;
            }
        }
        return false;
    }
    public boolean setCoordinateInput(String latitude,String longitude){
        location = restTemplate.getForObject("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude +"," + longitude +"&key=AIzaSyAUpsmOT1CxyhSZ9EN_DXaIsXiA-dBBqlM",GeocodeResponse.class);
        if(location.status.equals("OK")){
            return true;
        }
        return false;
    }

    public Double getLatitude(){
        return Double.parseDouble(location.results[0].geometry.location.lat);

    }
    public Double getLongitude(){
        return Double.parseDouble(location.results[0].geometry.location.lng);
    }
    public String getLocationName(){
        return location.results[0].formatted_address;
    }
}
