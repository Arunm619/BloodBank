package com.atribus.bloodbankyrc.Utils;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

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
    String userblood;

    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;


    public FirebaseClientDonations(Context c, String DB_URL, ListView listView, String order) {
        this.c = c;
        this.DB_URL = DB_URL;
        this.listView = listView;
        this.order = order;


        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);

        prefs = c.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        userblood = prefs.getString("blood", "else");

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

    private void getupdates(DataSnapshot dataSnapshot) {

        requestArrayList.clear();


        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Request d = ds.getValue(Request.class);


            Rules rules = new Rules();


            if (rules.rules(userblood, d.getRequiredbloodgroup()))
            {Toast.makeText(c, "UserBlood : "+userblood
                        +
                        "\n" +
                                "Required blood gorup : "+d.getRequiredbloodgroup()
                        , Toast.LENGTH_SHORT).show();
                requestArrayList.add(d);}


            if (requestArrayList.size() > 0) {

                if (order.equals("reverse"))
                    Collections.reverse(requestArrayList);
                donationAdapter = new DonationAdapter(c, requestArrayList);
                listView.setAdapter(donationAdapter);

                donationAdapter.notifyDataSetChanged();
                Utility.setDynamicHeight(listView);

            } else {
                Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
