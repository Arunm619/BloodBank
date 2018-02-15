package com.atribus.bloodbankyrc;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Adapters.RVAdapter;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.Model.UserDistanceDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminMain extends AppCompatActivity {
    MaterialEditText et_bloodgroup, et_location;
    Button btn_search;
    String location, requiredbloodgroup;
    float Distance = 0;
    Double xlat = 0.0, xlon = 0.0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");
    DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");
    List <UserDistanceDetails> nearbyDonors;
    private String TaG = "ArMa checks";


    private RecyclerView recyclerView;
    private RVAdapter rAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        nearbyDonors = new ArrayList <>();


        et_bloodgroup = findViewById(R.id.et_bloodgroup);
        et_location = findViewById(R.id.et_location);
        btn_search = findViewById(R.id.btn_search);
        bloodgroupsetter();


        recyclerView = findViewById(R.id.rv_donordetails);

        rAdapter = new RVAdapter(nearbyDonors);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(rAdapter);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyDonors.clear();

                searchusers();

            }
        });


    }

    private void searchusers() {
        requiredbloodgroup = et_bloodgroup.getText().toString().trim();

        location = et_location.getText().toString().trim();

        if (requiredbloodgroup.length() == 0) {
            Toast.makeText(this, "Choose Blood Type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location.length() == 0) {
            Toast.makeText(this, "Enter location Eg. Hospital name, location", Toast.LENGTH_SHORT).show();
            return;
        }


        //get lat long from location Double lat , long
        //the value is returned in xlat and xlon
        getlatlongfromplacename(location);
        //xlat,xlon


        UsersNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    Distance = 0;
                    User user = childSnapshot.getValue(User.class);

                    //Toast.makeText(AdminMain.this, "User name"+user.getName(), Toast.LENGTH_SHORT).show();
                    Double currlat = user.getLattitude();
                    Double currlong = user.getLongitude();

                    //calculates the distance from xlat,xlon to currlat,curlon
                    Distance = finddistance(currlat, currlong);
                    //Toast.makeText(AdminMain.this, Distance+"", Toast.LENGTH_SHORT).show();

                    if (user.getBloodgroup().equals(requiredbloodgroup)) {
                        UserDistanceDetails details = new UserDistanceDetails(user, Distance);
                        nearbyDonors.add(details);

                    }
                }

                Collections.sort(nearbyDonors, new Comparator <UserDistanceDetails>() {
                    @Override
                    public int compare(UserDistanceDetails u1, UserDistanceDetails u2) {
                        return Float.compare(u1.getDistance(), u2.getDistance());

                    }


                });

                rAdapter.notifyDataSetChanged();

                if (nearbyDonors.size() == 0) {
                    Toast.makeText(AdminMain.this, "No users Found.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminMain.this, nearbyDonors.size() + " users Found.", Toast.LENGTH_SHORT).show();

                }


                //finishes searching the total userlist


                //we now have sorted list of people near us.
                //we can print them
                //we gonna send them msg


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //if they are willing they wil text back


    }

    private float finddistance(Double currlat, Double currlong) {

        double latitude = currlat;
        double longitude = currlong;

        float distance = 0;
        //this is the location of Mr.X donors address with curlat,currlon
        Location Donor = new Location("crntlocation");
        Donor.setLatitude(latitude);
        Donor.setLongitude(longitude);

        //This is the location of the name you have typed in the location field
        Location Hospital = new Location("Hospital");
        Hospital.setLatitude(xlat);
        Hospital.setLongitude(xlon);


//float distance = crntLocation.distanceTo(newLocation);  in meters
        distance = Donor.distanceTo(Hospital) / 1000; // in km
        //    Toast.makeText(this, "" + distance, Toast.LENGTH_SHORT).show();
        return distance;
    }

    private void getlatlongfromplacename(String location) {

        xlat = 0.0;
        xlon = 0.0;
        if (Geocoder.isPresent()) {
            try {
                ;
                Geocoder gc = new Geocoder(this);
                List <Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                List <LatLng> ll = new ArrayList <LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        xlat = a.getLatitude();
                        xlon = a.getLongitude();
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }

    }

    private void bloodgroupsetter() {
        et_bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //material dialog to show all the blood groups

                new MaterialDialog.Builder(AdminMain.this)
                        .title(R.string.chooseblood)
                        .items(R.array.bloodtypes)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_bloodgroup.setTextColor(getResources().getColor(R.color.Red));
                                et_bloodgroup.setText(text);
                            }
                        })
                        .show();


            }
        });

    }

}
