package com.atribus.bloodbankyrc.Utils;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.atribus.bloodbankyrc.Adapters.CustomAdapter;
import com.atribus.bloodbankyrc.Model.post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FirebaseClient {

    Context c;
    String DB_URL;
    ListView listView;
    ProgressBar pb;
    View emptyView;
    ArrayList <post> postslist = new ArrayList <>();
    CustomAdapter customAdapter;
    DatabaseReference db;
    String order;

    public FirebaseClient(Context c, String DB_URL, ListView listView, String order, View emptyView, ProgressBar pb) {
        this.c = c;
        this.DB_URL = DB_URL;
        this.listView = listView;
        this.order = order;
        this.emptyView = emptyView;
        this.pb = pb;


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

    private void getupdates(DataSnapshot dataSnapshot) {
        postslist.clear();

        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            post d = dataSnapshot1.getValue(post.class);


            emptyView.setVisibility(View.GONE);
            postslist.add(d);
        }


        if (postslist.size() > 0) {

            if (order.equals("reverse"))
                Collections.reverse(postslist);
            customAdapter = new CustomAdapter(c, postslist);

            listView.setAdapter(customAdapter);
            pb.setVisibility(View.GONE);
            Utility.setDynamicHeight(listView);

        } else {
            pb.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
         //   Toast.makeText(c, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
