package com.atribus.bloodbankyrc.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.post;
import com.atribus.bloodbankyrc.PostDetailed;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.FirebaseClient;
import com.atribus.bloodbankyrc.Utils.FirebaseClientDonations;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
public class DonateBlood extends Fragment {

    private final String DB_URL = "https://bloodbank-3c1dd.firebaseio.com/Request/";
    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String temp = null;

    ListView Donatebloodfeed;
    private FirebaseClientDonations firebaseClientDonations;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Requestsnode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Request");

    public DonateBlood() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_donate_blood, container, false);

        Donatebloodfeed = v.findViewById(R.id.lvDonatebloodfeed);
        prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        String userblood = prefs.getString("blood", "else");

        String temp =DB_URL;


        //user is O+ blood
        //show him all the lists of users who can take blood from O+
        //https://bloodbank-3c1dd.firebaseio.com/Request/O+ contains all requests with O+


        //O+


        firebaseClientDonations = new FirebaseClientDonations(getActivity(), temp, Donatebloodfeed, "reverse");
        firebaseClientDonations.refreshdata();


        Donatebloodfeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int i, long l) {
                Request post = (Request) adapterView.getItemAtPosition(i);
                Gson gson = new Gson();
                String objstring = gson.toJson(post);
                Toast.makeText(getActivity(), objstring, Toast.LENGTH_SHORT).show();
                //                Intent intent = new Intent(getContext(),PostDetailed.class);
//                intent.putExtra("obj", objstring);
//                //  getActivity().finish();
//                startActivity(intent);

                //Toast.makeText(getActivity(), objstring, Toast.LENGTH_SHORT).show();

            }
        });

        return v;

    }
}