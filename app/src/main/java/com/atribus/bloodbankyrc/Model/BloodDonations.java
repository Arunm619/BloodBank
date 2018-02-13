package com.atribus.bloodbankyrc.Model;

/**
 * Created by root on 13/2/18.
 */

public class BloodDonations {

    String hostpitalname;
    String hospitalocation;
    String uuid;
    String date;


    public String getHostpitalname() {
        return hostpitalname;
    }

    public void setHostpitalname(String hostpitalname) {
        this.hostpitalname = hostpitalname;
    }

    public String getHospitalocation() {
        return hospitalocation;
    }

    public void setHospitalocation(String hospitalocation) {
        this.hospitalocation = hospitalocation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String UUID) {
        this.uuid = UUID;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BloodDonations() {

    }
}

