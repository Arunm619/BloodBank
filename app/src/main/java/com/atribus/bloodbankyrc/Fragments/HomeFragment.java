package com.atribus.bloodbankyrc.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Model.post;
import com.atribus.bloodbankyrc.PostDetailed;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.FirebaseClient;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;


public class HomeFragment extends Fragment {

    private static final String DB_URL = "https://bloodbank-3c1dd.firebaseio.com/PostDetails";

    ListView Homefeed;
    private FirebaseClient firebaseClient;

    public HomeFragment() {
        // Required empty public constructor
    }
    CardView cvempty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        cvempty=v.findViewById(R.id.cvempty);

        Homefeed = v.findViewById(R.id.lvHomefeed);

        firebaseClient = new FirebaseClient(getActivity(), DB_URL, Homefeed, "reverse",cvempty);
        firebaseClient.refreshdata();

        Homefeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int i, long l) {
                post post = (post) adapterView.getItemAtPosition(i);
                Gson gson = new Gson();
                String objstring = gson.toJson(post);
                Intent intent = new Intent(getContext(),PostDetailed.class);
                intent.putExtra("obj", objstring);
                //  getActivity().finish();
                startActivity(intent);

                //Toast.makeText(getActivity(), objstring, Toast.LENGTH_SHORT).show();

            }
        });

        return v;
    }

}