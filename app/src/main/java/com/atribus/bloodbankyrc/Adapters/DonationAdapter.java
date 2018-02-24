package com.atribus.bloodbankyrc.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.DonateHolder;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 11/2/18.
 */

public class DonationAdapter extends BaseAdapter {
    private String MY_PREFS_NAME = "MYDB";
    private SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private DonateHolder donateHolder;
    private Context c;
    private ArrayList <Request> requests;
    private LayoutInflater inflater;

    public DonationAdapter(Context c, ArrayList <Request> requests) {
        this.c = c;
        this.requests = requests;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int i) {
        return requests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (inflater == null)
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            if (inflater != null) {
                view = inflater.inflate(R.layout.donaterequest_row, viewGroup, false);
            }

         donateHolder = new DonateHolder(view);
        String message = " " + requests.get(i).getName() + " " + "Requires " +
                requests.get(i).getRequiredunits() + " Unit of " + requests.get(i).getRequiredbloodgroup() +
                " at " + requests.get(i).getLocation() + " ";
        donateHolder.tvdescription.setText(message);

        Double xlat = 0.0, xlon = 0.0;
        try {
            xlat = requests.get(i).getLattitude();
            xlon = requests.get(i).getLongitude();
        } catch (Exception e) {
            Toast.makeText(c, "msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Double userlat;
        Double userlon;


        prefs = c.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        editor = prefs.edit();


        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);

        if (obj != null) {

            userlat = obj.getLattitude();
            userlon = obj.getLongitude();

            float dis = finddistance(userlat, userlon, xlat, xlon);

            if (dis > 1) {
                @SuppressLint("DefaultLocale") String s = String.format("%.2f", dis);
                donateHolder.tvneardistance.setText(s + " kms away");
            } else {
                float metres = dis * 1000;
                @SuppressLint("DefaultLocale") String s = String.format("%f", metres);

                donateHolder.tvneardistance.setText(s + " meters away");


            }
         //  setimage(requests.get(i).getRequiredbloodgroup());
            //  Toast.makeText(getActivity(), "Mobile :" + mobilenumber, Toast.LENGTH_SHORT).show();
        } else {

            donateHolder.tvneardistance.setVisibility(View.GONE);

        }


        return view;
    }

   /* private void setimage(String bloodgroup) {
        switch (bloodgroup)
        {
            case "AB+":
                donateHolder.img.setImageResource(R.drawable.abpos);
                break;
            case "AB-":
                donateHolder.img.setImageResource(R.drawable.abneg);
                break;

            case "A+":
                donateHolder.img.setImageResource(R.drawable.apos);
                break;
            case "A-":
                donateHolder.img.setImageResource(R.drawable.aneg);
                break;

            case "B+":
                donateHolder.img.setImageResource(R.drawable.bpos);
                break;
            case "B-":
                donateHolder.img.setImageResource(R.drawable.bneg);
                break;

            case "O+":
                donateHolder.img.setImageResource(R.drawable.opos);
                break;
            case "O-":
                donateHolder.img.setImageResource(R.drawable.oneg);
                break;

        }
        donateHolder.img.setImageResource(R.drawable.abpos);
    }*/

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    private float finddistance(Double userlat, Double userlong, Double xlat, Double xlong) {

        double latitude = userlat;
        double longitude = userlong;

        float distance;
        //this is the location of Mr.X donors address with curlat,currlon
        Location Donor = new Location("crntlocation");
        Donor.setLatitude(latitude);
        Donor.setLongitude(longitude);

        //This is the location of the name you have typed in the location field
        Location Hospital = new Location("Hospital");
        Hospital.setLatitude(xlat);
        Hospital.setLongitude(xlong);


//float distance = crntLocation.distanceTo(newLocation);  in meters
        distance = Donor.distanceTo(Hospital) / 1000; // in km
        //    Toast.makeText(this, "" + distance, Toast.LENGTH_SHORT).show();
        return distance;
    }

}

/*public class DonationAdapter extends BaseAdapter {

    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    Context c;
    ArrayList <Request> requests;
    LayoutInflater inflater;


    public DonationAdapter(Context c, ArrayList <Request> requests) {
        this.c = c;
        this.requests = requests;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int i) {
        return requests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (inflater == null)
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.donaterequest_row, viewGroup, false);

        DonateHolder donateHolder = new DonateHolder(view);
        String message = " " + requests.get(i).getName() + " " + "Requires " +
                requests.get(i).getRequiredunits() + " Unit of " + requests.get(i).getRequiredbloodgroup() +
                " at" + requests.get(i).getLocation() + " ";
        donateHolder.tvdescription.setText(message);

    /*    Double xlat=0.0,xlon=0.0;
       try {
            xlat = requests.get(i).getLattitude();
            xlon = requests.get(i).getLongitude();
       }
       catch (Exception e )
       {
           Toast.makeText(c, "msg: " +e.getMessage(), Toast.LENGTH_SHORT).show();
       }

        Double userlat;
        Double userlon;


        prefs = c.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        editor = prefs.edit();


        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj != null) {

            userlat = obj.getLattitude();
            userlon = obj.getLongitude();
            float dis = finddistance(userlat, userlon, xlat, xlon);


            donateHolder.tvneardistance.setText(dis + " kms away");

            //  Toast.makeText(getActivity(), "Mobile :" + mobilenumber, Toast.LENGTH_SHORT).show();
        } else {

            donateHolder.tvneardistance.setVisibility(View.GONE);

        }


        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }


   /* private float finddistance(Double userlat, Double userlong, Double xlat, Double xlong) {

        double latitude = userlat;
        double longitude = userlong;

        float distance = 0;
        //this is the location of Mr.X donors address with curlat,currlon
        Location Donor = new Location("crntlocation");
        Donor.setLatitude(latitude);
        Donor.setLongitude(longitude);

        //This is the location of the name you have typed in the location field
        Location Hospital = new Location("Hospital");
        Hospital.setLatitude(xlat);
        Hospital.setLongitude(xlong);


//float distance = crntLocation.distanceTo(newLocation);  in meters
        distance = Donor.distanceTo(Hospital) / 1000; // in km
        //    Toast.makeText(this, "" + distance, Toast.LENGTH_SHORT).show();
        return distance;
    }

}
*/

