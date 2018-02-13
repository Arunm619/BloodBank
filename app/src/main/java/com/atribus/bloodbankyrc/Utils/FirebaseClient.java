package com.atribus.bloodbankyrc.Utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Adapters.CustomAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.atribus.bloodbankyrc.Model.post;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;

public class FirebaseClient {

    Context c;
    String DB_URL;
    ListView listView;
    CardView cv_empty;
    ArrayList <post> postslist = new ArrayList <>();
    CustomAdapter customAdapter;
    DatabaseReference db;
    String order;

    public FirebaseClient(Context c, String DB_URL, ListView listView, String order, CardView cv_empty) {
        this.c = c;
        this.DB_URL = DB_URL;
        this.listView = listView;
        this.order = order;
        this.cv_empty = cv_empty;


        db = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL);
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

    public void getupdates(DataSnapshot dataSnapshot) {
        postslist.clear();

        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            post d = dataSnapshot1.getValue(post.class);

            postslist.add(d);
            cv_empty.setVisibility(View.GONE);


            if (postslist.size() > 0) {

                if (order.equals("reverse"))
                    Collections.reverse(postslist);
                customAdapter = new CustomAdapter(c, postslist);
                listView.setAdapter(customAdapter);
                Utility.setDynamicHeight(listView);

            } else {
                cv_empty.setVisibility(View.VISIBLE);
                Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}