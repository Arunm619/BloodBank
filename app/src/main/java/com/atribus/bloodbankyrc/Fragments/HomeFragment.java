package com.atribus.bloodbankyrc.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.atribus.bloodbankyrc.Model.post;
import com.atribus.bloodbankyrc.PostDetailed;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.FirebaseClient;
import com.google.gson.Gson;


public class HomeFragment extends Fragment {

    private static final String DB_URL = "https://bloodbank-3c1dd.firebaseio.com/PostDetails";

    ListView Homefeed;
    ProgressBar prgbar_home;
    private FirebaseClient firebaseClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Homefeed = v.findViewById(R.id.lvHomefeed);

        //cardview of empty is not shown at first
        emptyView = v.findViewById(R.id.emptyview);
        emptyView.setVisibility(View.INVISIBLE);

        //progress bar is loading...
        prgbar_home = v.findViewById(R.id.prgbar_home);
        prgbar_home.setVisibility(View.VISIBLE);

        firebaseClient = new FirebaseClient(getActivity(), DB_URL, Homefeed, "reverse", emptyView,prgbar_home);
        firebaseClient.refreshdata();

        Homefeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int i, long l) {
                post post = (post) adapterView.getItemAtPosition(i);
                Gson gson = new Gson();
                String objstring = gson.toJson(post);
                Intent intent = new Intent(getContext(), PostDetailed.class);
                intent.putExtra("obj", objstring);
                //  getActivity().finish();
                startActivity(intent);

                //Toast.makeText(getActivity(), objstring, Toast.LENGTH_SHORT).show();

            }
        });

        return v;
    }

}
