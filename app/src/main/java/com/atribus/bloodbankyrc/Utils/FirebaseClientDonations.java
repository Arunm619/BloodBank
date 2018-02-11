package com.atribus.bloodbankyrc.Utils;

import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Adapters.CustomAdapter;
import com.atribus.bloodbankyrc.Adapters.DonationAdapter;
import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by root on 11/2/18.
 */

public class FirebaseClientDonations {
    Context c;
    String DB_URL;
    ListView listView;
    ArrayList <Request> requestArrayList = new ArrayList <>();
    DonationAdapter donationAdapter;
    DatabaseReference db;
    String order;

    public FirebaseClientDonations(Context c, String DB_URL, ListView listView, String order) {
        this.c = c;
        this.DB_URL = DB_URL;
        this.listView = listView;
        this.order = order;


        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
    }

    public void refreshdata() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getupdates(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getupdates(DataSnapshot dataSnapshot) {


        Request d = dataSnapshot.getValue(Request.class);

        requestArrayList.add(d);


        if (requestArrayList.size() > 0) {

            if (order.equals("reverse"))
                Collections.reverse(requestArrayList);
            donationAdapter = new DonationAdapter(c, requestArrayList);
            listView.setAdapter(donationAdapter);
            Utility.setDynamicHeight(listView);

        } else {
            Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
        }
    }

}
