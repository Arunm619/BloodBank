package com.atribus.bloodbankyrc.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.FirebaseClientDonations;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    View emptyview;
    ProgressBar prgbar_donations;

    public DonateBlood() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_donate_blood, container, false);

        Donatebloodfeed = v.findViewById(R.id.lvDonatebloodfeed);

        //cardview of empty is not shown at first
        emptyview = v.findViewById(R.id.emptyview);
        emptyview.setVisibility(View.GONE);

        //progress bar is loading...
        prgbar_donations = v.findViewById(R.id.prgbar_donations);
        prgbar_donations.setVisibility(View.VISIBLE);

        prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        String userblood = prefs.getString("blood", "else");

        String temp = DB_URL;


        //user is O+ blood
        //show him all the lists of users who can take blood from O+
        //https://bloodbank-3c1dd.firebaseio.com/Request/O+ contains all requests with O+


        //O+


        firebaseClientDonations = new FirebaseClientDonations(getActivity(), temp, Donatebloodfeed, "reverse", emptyview, prgbar_donations);
        firebaseClientDonations.refreshdata();


        Donatebloodfeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int i, long l) {
                final Request request = (Request) adapterView.getItemAtPosition(i);
//                Gson gson = new Gson();
//                String objstring = gson.toJson(post);
//                Toast.makeText(getActivity(), objstring, Toast.LENGTH_SHORT).show();
                //                Intent intent = new Intent(getContext(),PostDetailed.class);
//                intent.putExtra("obj", objstring);
//                //  getActivity().finish();
//                startActivity(intent);

                //Toast.makeText(getActivity(), objstring, Toast.LENGTH_SHORT).show();


                //String[] Title = {"Name", "Phone", "College", "Dept"};
                String[] Details = {"Name : " + request.getName()
                        , "Phone : " + request.getMobilenumber(),
                        "Location  : " + request.getLocation(),
                        "Message  : " + request.getMessage()

                };


                new MaterialDialog.Builder(getActivity())
                        .title("Details of " + request.getName())
                        .items(Details)
                        .positiveText("Share")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String Text = request.getName() + " Requires "
                                        + request.getRequiredunits() + " Units of "
                                        + request.getRequiredbloodgroup() + " Blood At "
                                        + request.getLocation()
                                        + "\n Contact : " + request.getMobilenumber();

                                String installapp = "\n \nSave lives, By installing " + getString(R.string.applink);

                                String message = Text + installapp;

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);


                            }
                        })
                        .negativeText("Call")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + String.valueOf(request.getMobilenumber())));
                                startActivity(intent);

                            }
                        })
                        .neutralText("Show On Map")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Double lat = request.getLattitude();
                                Double lon = request.getLongitude();
                                String Locationname = request.getName() + "'s Here!";
                                Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + Uri.encode(Locationname));
                                final String BrowserURI = "https://www.google.co.in/maps?" + gmmIntentUri.toString();


                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                } else {
                                    Snackbar
                                            .make(v, "Maps Not Found! Want to open on Browser?", Snackbar.LENGTH_LONG)
                                            .setAction("Open", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(BrowserURI));
                                                    startActivity(i);
                                                }
                                            })
                                            .show();

                                }
                            }
                        })
                        .show();


            }
        });

        return v;

    }
}