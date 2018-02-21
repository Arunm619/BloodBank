package com.atribus.bloodbankyrc.AdminPackage;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Adapters.RVAdapter;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.Model.UserDistanceDetails;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.PlaceArrayAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

public class AdminMain extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    private static final String LOG_TAG = "Arun checks";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));


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

    private RelativeLayout rl_adminmain;
    private RecyclerView recyclerView;
    private RVAdapter rAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        getSupportActionBar().setTitle("Search Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        nearbyDonors = new ArrayList <>();

        rl_adminmain = findViewById(R.id.rl_adminmain);
        et_bloodgroup = findViewById(R.id.et_bloodgroup);
        et_location = findViewById(R.id.et_location);
        btn_search = findViewById(R.id.btn_search);
        bloodgroupsetter();


        //Autocomplete
        mGoogleApiClient = new GoogleApiClient.Builder(AdminMain.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(AdminMain.this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = findViewById(R.id
                .et_autocomplete);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_INDIA, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);


        recyclerView = findViewById(R.id.rv_donordetails);

        rAdapter = new RVAdapter(nearbyDonors);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        recyclerView.setAdapter(rAdapter);

        rAdapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //     Toast.makeText(AdminMain.this, "" + nearbyDonors.get(position).getUser().getName(), Toast.LENGTH_SHORT).show();

                UserDistanceDetails details = nearbyDonors.get(position);

                float kmsaway = details.getDistance();
                User user = details.getUser();
                final String name = user.getName();
                final String phonenumber = user.getMobilenumber().toString();
                final String bloodgroup = user.getBloodgroup();
                final String Address = user.getAddress();
                final String Gender = user.getGender();
                final String dateofbirth = user.getDateofbirth();
                final String age = String.valueOf(user.getAge());
                final Double lat = user.getLattitude();
                final Double lon = user.getLongitude();
                final String dateofregistration = user.getDateofregistration();


                String[] Details = {"Name : " + name
                        , "Phone : " + phonenumber,
                        "Blood group  : " + bloodgroup,
                        "Location  : " + Address,
                        "Gender : " + Gender,
                        "Age : " + age,
                        "D.O.B  : " + dateofbirth,


                };


                new MaterialDialog.Builder(AdminMain.this)
                        .title("Details of " + name)
                        .items(Details)
                        .positiveText("Share")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                String Text =
                                        "Details of " + name + "\n \n " +
                                                "Name : " + name + "\n " +
                                                "Phone : " + phonenumber + "\n " +
                                                "Blood group : " + bloodgroup + "\n " +
                                                "Location : " + Address + "\n " +
                                                "Gender : " + Gender + "\n " +
                                                "Age : " + age + "\n " +
                                                "D.O.B : " + dateofbirth + "\n \n \n ";


                                String installapp = "\n \nSave lives, By installing " + getString(R.string.applink);

                                String message = Text;

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

                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phonenumber));
                                startActivity(intent);

                            }
                        })
                        .neutralText("Show On Map")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                         /*       String Locationname = name + "'s Here!";
                                Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + Uri.encode(Locationname));
                                final String BrowserURI = "https://www.google.co.in/maps?" + gmmIntentUri.toString();


                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                } else {
                                    Snackbar
                                            .make(rl_adminmain, "Maps Not Found! Want to open on Browser?", Snackbar.LENGTH_LONG)
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
                   */
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + name + ")"));
                                startActivity(intent);

                            }
                        })
                        .show();


            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyDonors.clear();
                recyclerView.invalidate();
                rAdapter.notifyDataSetChanged();

                searchusers();

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        //  startActivity(new Intent(this, AdminMain.class));
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


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult <PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback <PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback <PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            et_location.setText(Html.fromHtml(place.getAddress() + ""));

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
   /* private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener <Location>() {
                    @Override
                    public void onComplete(@NonNull Task <Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            xlat = mLastLocation.getLatitude();
                            xlon = mLastLocation.getLongitude();
                            Toast.makeText(getActivity(), "" + mLastLocation.toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            //Toast.makeText(getActivity(), "Failed to track location", Toast.LENGTH_SHORT).show();

                            xlat = 0.0;
                            xlon = 0.0;
                            getlatlongfromplacename(location);
                        }
                    }
                });
    }

*/

    @Override
    public void onPause() {
        super.onPause();

        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();


    }
}
