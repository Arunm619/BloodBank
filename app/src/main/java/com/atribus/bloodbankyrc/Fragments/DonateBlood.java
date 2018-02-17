package com.atribus.bloodbankyrc.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
    CardView cvempty;
    ProgressBar prgbar_donations;

    public DonateBlood() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_donate_blood, container, false);

        Donatebloodfeed = v.findViewById(R.id.lvDonatebloodfeed);

        //cardview of empty is not shown at first
        cvempty = v.findViewById(R.id.cvempty);
        cvempty.setVisibility(View.GONE);

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


        firebaseClientDonations = new FirebaseClientDonations(getActivity(), temp, Donatebloodfeed, "reverse", cvempty, prgbar_donations);
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
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                                switch (which) {
//                                    case 1:
//                                        Toast.makeText(getActivity(), "" + dialog.getItems().get(which).toString(), Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + String.valueOf(request.getMobilenumber())));
//                                        startActivity(intent);
//                                        break;
//
//                                    case 4:
//
//                                        break;
//
//
//                                    default:
//                                        //   Toast.makeText(getActivity(), dialog.getItems().get(which).toString(), Toast.LENGTH_SHORT).show();
//
//
//                                }
//
//                            }
//                        })

                        .show();


            }
        });

        return v;

    }
}