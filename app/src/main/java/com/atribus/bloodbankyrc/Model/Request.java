package com.atribus.bloodbankyrc.Model;

/**
 * Created by root on 11/2/18.
 */

public class Request {
    public String name;
    public Long mobilenumber;
    public String location;
    public String message;
    public String requiredbloodgroup;
    public int requiredunits;
    public Double lattitude;
    public Double longitude;

    public Request() {
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                ", mobilenumber=" + mobilenumber +
                ", location='" + location + '\'' +
                ", message='" + message + '\'' +
                ", requiredbloodgroup='" + requiredbloodgroup + '\'' +
                ", requiredunits=" + requiredunits +
                ", lattitude=" + lattitude +
                ", longitude=" + longitude +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(Long mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequiredbloodgroup() {
        return requiredbloodgroup;
    }

    public void setRequiredbloodgroup(String requiredbloodgroup) {
        this.requiredbloodgroup = requiredbloodgroup;
    }

    public int getRequiredunits() {
        return requiredunits;
    }

    public void setRequiredunits(int requiredunits) {
        this.requiredunits = requiredunits;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
