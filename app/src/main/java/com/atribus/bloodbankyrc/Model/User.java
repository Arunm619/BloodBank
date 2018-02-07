package com.atribus.bloodbankyrc.Model;

import java.util.Date;

/**
 * Created by root on 7/2/18.
 */

public class User {

    String name;
    int age;
    Date dateofbirth;
    Date dateofregistration;
    String bloodgroup;
    String address;
    Long lattitude,longitude;
    Long mobilenumber;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(Date dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public Date getDateofregistration() {
        return dateofregistration;
    }

    public void setDateofregistration(Date dateofregistration) {
        this.dateofregistration = dateofregistration;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getLattitude() {
        return lattitude;
    }

    public void setLattitude(Long lattitude) {
        this.lattitude = lattitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Long getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(Long mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", dateofbirth=" + dateofbirth +
                ", dateofregistration=" + dateofregistration +
                ", bloodgroup='" + bloodgroup + '\'' +
                ", address='" + address + '\'' +
                ", lattitude=" + lattitude +
                ", longitude=" + longitude +
                ", mobilenumber=" + mobilenumber +
                '}';
    }
}
