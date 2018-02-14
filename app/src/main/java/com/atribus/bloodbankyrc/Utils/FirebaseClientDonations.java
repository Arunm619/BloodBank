package com.atribus.bloodbankyrc.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Adapters.CustomAdapter;
import com.atribus.bloodbankyrc.Adapters.DonationAdapter;
import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.Model.post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 * Created by root on 11/2/18.
 */

public class FirebaseClientDonations {
    private Context c;
    private ListView listView;
    private ArrayList <Request> requestArrayList = new ArrayList <>();
    private DatabaseReference db;
    private CardView cv_empty;
    private String order;
    private String userblood;
    private String Currmobilenumber;
    private ProgressBar pb;

    private Rules rules = new Rules();


    public FirebaseClientDonations(Context c, String DB_URL, ListView listView, String order, CardView cv_empty, ProgressBar pb) {
        this.c = c;
        this.listView = listView;
        this.order = order;
        this.cv_empty = cv_empty;
        this.pb = pb;


        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        String MY_PREFS_NAME = "MYDB";
        SharedPreferences prefs = c.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        userblood = prefs.getString("blood", "else");
        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj != null) {
            Currmobilenumber = String.valueOf(obj.getMobilenumber());

        }
    }

    public void refreshdata() {
        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

 /*   private void getupdates(DataSnapshot dataSnapshot) {

        requestArrayList.clear();


        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Request d = ds.getValue(Request.class);

            if (rules.rules(userblood, d.getRequiredbloodgroup())) {

                if (!String.valueOf(d.getMobilenumber()).equals(Currmobilenumber)) {
                    requestArrayList.add(d);

                }
            }

        }


        if (requestArrayList.size() > 0) {


            if (order.equals("reverse"))
                Collections.reverse(requestArrayList);
            DonationAdapter donationAdapter = new DonationAdapter(c, requestArrayList);


            pb.setVisibility(View.GONE);
            cv_empty.setVisibility(View.GONE);
            donationAdapter.notifyDataSetChanged();
            Utility.setDynamicHeight(listView);

        } else {

            pb.setVisibility(View.GONE);
            //listView.setVisibility(GONE);

            Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
            cv_empty.setVisibility(View.VISIBLE);
        }

    }


}*/


    private void getupdates(DataSnapshot dataSnapshot) {


        requestArrayList.clear();


        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Request d = ds.getValue(Request.class);


            if (rules.rules(userblood, d.getRequiredbloodgroup())) {

                if (!String.valueOf(d.getMobilenumber()).equals(Currmobilenumber))
                    requestArrayList.add(d);


            }
            cv_empty.setVisibility(View.GONE);
        }
            if (requestArrayList.size() > 0) {
                pb.setVisibility(View.GONE);

                if (order.equals("reverse"))
                    Collections.reverse(requestArrayList);
                DonationAdapter donationAdapter = new DonationAdapter(c, requestArrayList);
                listView.setAdapter(donationAdapter);

                donationAdapter.notifyDataSetChanged();
                Utility.setDynamicHeight(listView);

            } else {

                pb.setVisibility(View.GONE);
             //   Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
                cv_empty.setVisibility(View.VISIBLE);
            }

        }

    }

