package com.atribus.bloodbankyrc.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by root on 10/2/18.
 */

public class UserDistanceDetails implements Parcelable,Serializable {
    private User user;
    private float distance;

    UserDistanceDetails() {
    }

    public UserDistanceDetails(User user, float distance) {
        this.user = user;
        this.distance = distance;
    }

    protected UserDistanceDetails(Parcel in) {
        distance = in.readFloat();
    }

    public static final Creator <UserDistanceDetails> CREATOR = new Creator <UserDistanceDetails>() {
        @Override
        public UserDistanceDetails createFromParcel(Parcel in) {
            return new UserDistanceDetails(in);
        }

        @Override
        public UserDistanceDetails[] newArray(int size) {
            return new UserDistanceDetails[size];
        }
    };

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(distance);
    }
}
