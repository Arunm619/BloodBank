package com.atribus.bloodbankyrc.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.Message;
import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.NoInternet;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RequestBlood extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "Arun checks";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

    FirebaseDatabase database;
    DatabaseReference requestNode, successfulrequest;

    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;


    SharedPreferences.Editor editor;

    public RequestBlood() {
        // Required empty public constructor
    }

    MaterialEditText et_bloodgroup, et_bloodunits, et_name,
            et_mobilenumber, et_location, et_message;

    String bloodgroup, bloodunits, name, location, message, hospitalname;
    Button btn_request;
    Snackbar snackbar;
    String placeId;
    View v;
    Double xlat = 0.0, xlon = 0.0;

    RelativeLayout rlbloodrequest, rlbloodrequested;
    Button btnedit, btncancel, btnSuccess;
    String mobilenumber;
    TextView tvname, tvmobile, tvlocation, tvmessage, tvbloodgroup, tvbloodunits;

    FirebaseDatabase databasenotifications;
    DatabaseReference myRef;


    //  private FusedLocationProviderClient mFusedLocationClient;
    // protected Location mLastLocation;
    Context c;


    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databasenotifications = FirebaseDatabase.getInstance();
        myRef = databasenotifications.getReference("messages");
        c = container.getContext();
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_request_blood, container, false);
        //  mFusedLocationClient = LocationServices.getFusedLocationProviderClient(c);

        if (!isNetworkAvailable()) {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        // String MY_PREFS_NAME = "MYDB";

        prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        // editor.clear();

        getmobilenumberfromUserObj();
        // getLastLocation();

        database = FirebaseDatabase.getInstance();
        requestNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Request");
        successfulrequest = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/SuccessRequest");

        //autocomplete
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = v.findViewById(R.id
                .et_autocomplete);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_INDIA, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        rlbloodrequest = v.findViewById(R.id.rlrequestblood);
        rlbloodrequested = v.findViewById(R.id.rlrequestedblood);


        et_bloodgroup = v.findViewById(R.id.et_bloodgroup);
        et_bloodunits = v.findViewById(R.id.et_bloodunits);
        et_mobilenumber = v.findViewById(R.id.et_mobilenumber);
        et_name = v.findViewById(R.id.et_name);
        et_location = v.findViewById(R.id.et_location);
        et_message = v.findViewById(R.id.et_message);

        tvbloodgroup = v.findViewById(R.id.tvblood);
        tvname = v.findViewById(R.id.tvName);
        tvmessage = v.findViewById(R.id.tvmessage);
        tvbloodunits = v.findViewById(R.id.tvbloodunits);
        tvlocation = v.findViewById(R.id.tvlocation);
        tvmobile = v.findViewById(R.id.tvmobile);

        btn_request = v.findViewById(R.id.btn_request);
        btncancel = v.findViewById(R.id.btnCancelreq);
        btnedit = v.findViewById(R.id.btnEdit);
        btnSuccess = v.findViewById(R.id.btnSuccess);

        Gson g = new Gson();
        String objasstring = prefs.getString("ReqObj", "");


        Request req = g.fromJson(objasstring, Request.class);
        if (req != null) {
            setrequestedscreenon(req);
        }


        bloodgroupsetter();

/*
        et_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //    startActivity(new Intent(getActivity(),PlacesAutoComplete.class));
            }
        });*/
        et_mobilenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "You cannot change the mobilenumber here \n" +
                        " Go to [Settings] -> Edit Profile -> change mobile number there", Toast.LENGTH_SHORT).show();
            }
        });

        et_mobilenumber.setText(mobilenumber);


        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectdetails();
            }
        });

        return v;
    }

    private void deletetherequest(Request request) {

        requestNode/*.child(request.getRequiredbloodgroup())*/.child(String.valueOf(request.getMobilenumber())).removeValue();
        editor.remove("ReqObj");
        editor.apply();
        openeditscreen();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    void openeditscreen() {
        rlbloodrequested.setVisibility(View.GONE);
        rlbloodrequest.setVisibility(View.VISIBLE);

        et_bloodgroup.setText("");
        et_message.setText("");
        et_name.setText("");
        et_bloodunits.setText("");
        et_mobilenumber.setText(mobilenumber);
    }

    private void getmobilenumberfromUserObj() {
        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj != null) {
            mobilenumber = String.valueOf(obj.getMobilenumber());
            //  Toast.makeText(getActivity(), "Mobile :" + mobilenumber, Toast.LENGTH_SHORT).show();
        }

    }

    private void collectdetails() {

        int a = nullcheck();
        if (a == -1)
            return;
        if (isNetworkAvailable())
            dorequest();
        else {
            Toast.makeText(c, "Check Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void dorequest() {


        Request request = new Request();
        request.setName(name);
        request.setMessage(message);
        request.setMobilenumber(Long.valueOf(mobilenumber));
        request.setRequiredunits(Integer.parseInt(bloodunits));
        request.setRequiredbloodgroup(bloodgroup);
        request.setLocation(location);
        request.setLattitude(xlat);
        request.setLongitude(xlon);
        //  Toast.makeText(getActivity(), "" + request.toString(), Toast.LENGTH_SHORT).show();


        //request node -> blood group -> id --Mobilenumber -> Data Request
        requestNode/*.child(request.getRequiredbloodgroup())*/.child(String.valueOf(request.getMobilenumber())).setValue(request);
        Toast.makeText(getActivity(), "Successfully requested. Please wait while our donors call you.", Toast.LENGTH_SHORT).show();
        setrequestedscreenon(request);

        //sending notifcations to all the users
        //title , message
        myRef.push().setValue(new Message(bloodgroup, mobilenumber));


        //storing offline copy of ReqObj in sharedpred under MYDB
        Gson gson = new Gson();
        String reqobjasstring = gson.toJson(request);
        editor.putString("ReqObj", reqobjasstring);
        editor.commit();

    }

    private void setrequestedscreenon(final Request request) {

        rlbloodrequest.setVisibility(View.GONE);

        //Split the Bolded Strings And Requests Strings

        rlbloodrequested.setVisibility(View.VISIBLE);


        tvmobile.setText(makebold("Mobile : ", String.valueOf(request.getMobilenumber())));
        tvname.setText(makebold("Name : ", request.getName()));
        tvlocation.setText(makebold("Location : ", request.getLocation()));
        tvbloodunits.setText(makebold("Units : ", String.valueOf(request.getRequiredunits())));
        tvbloodgroup.setText(makebold("Blood Group : ", request.getRequiredbloodgroup()));
        tvmessage.setText(makebold("Message : ", request.getMessage()));

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
                //open that screen with all the data
                //thats all

                // requestobject


                if (isNetworkAvailable()) {
                    openeditscreen(request);

                } else {
                    Snackbar.make(rlbloodrequest, "Check Internet ", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.Cancel)
                            .content(R.string.cancelcontent)
                            .positiveText(R.string.agree)
                            .negativeText(R.string.disagree)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    deletetherequest(request);
                                }
                            })
                            .show();
                } else {
                    Snackbar.make(rlbloodrequest, "Check Internet ", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //delete the node from RequestsPending


                if (isNetworkAvailable()) {

                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.Gottheblood)
                            .content(R.string.gottheblooddesc)
                            .positiveText(R.string.agree)
                            .negativeText(R.string.disagree)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    successfulrequest.child(String.valueOf(request.getMobilenumber())).push().setValue(request);

                                    deletetherequest(request);
                                    // Toast.makeText(getActivity(), "Get Well Soon ", Toast.LENGTH_SHORT).show();

                                    Snackbar.make(v, "Get Well Soon :)", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .show();


                } else {
                    Snackbar.make(rlbloodrequest, "Check Internet ", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    private SpannableString makebold(String Title, String Result) {

        SpannableString str = new SpannableString(Title + Result);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, Title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    private void openeditscreen(Request request) {
        rlbloodrequested.setVisibility(View.GONE);
        rlbloodrequest.setVisibility(View.VISIBLE);

        et_name.setText(request.getName());
        et_location.setText(request.getLocation());
        et_bloodunits.setText(String.valueOf(request.getRequiredunits()));
        et_message.setText(request.getMessage());

        et_bloodgroup.setText(request.getRequiredbloodgroup());
        et_mobilenumber.setText(String.valueOf(request.getMobilenumber()));
        mAutocompleteTextView.setText(request.getLocation());
    }

    private int nullcheck() {
        bloodgroup = et_bloodgroup.getText().toString().trim();
        bloodunits = et_bloodunits.getText().toString().trim();
        name = et_name.getText().toString().trim();
        mobilenumber = et_mobilenumber.getText().toString().trim();
        location = et_location.getText().toString().trim();
        getlatlongfromplacename(location);
        message = et_message.getText().toString().trim();

        if (TextUtils.isEmpty(bloodgroup)) {
            snackbar = Snackbar
                    .make(v, "Please choose blood group", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }
        if (TextUtils.isEmpty(bloodunits)) {
            snackbar = Snackbar
                    .make(v, "Please enter blood units ", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }

        if (Integer.parseInt(bloodunits) == 0 || Integer.parseInt(bloodunits) > 9) {
            snackbar = Snackbar
                    .make(v, "Enter valid units", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }

        if (TextUtils.isEmpty(mobilenumber)) {
            snackbar = Snackbar
                    .make(v, "Please enter mobile number ", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }

        if (mobilenumber.length() != 10) {
            snackbar = Snackbar
                    .make(v, "Please enter correct mobile number ", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }

        if (TextUtils.isEmpty(name)) {
            snackbar = Snackbar
                    .make(v, "Please enter name", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }
        if (TextUtils.isEmpty(location)) {
            snackbar = Snackbar
                    .make(v, "Please enter location ", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }

        if (TextUtils.isEmpty(message)) {
            snackbar = Snackbar
                    .make(v, "Please enter message for additional info ", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;
        }

        if (xlat == 0.0 && xlon == 0.0) {
            // getlatlongfromplacename(location);
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback <PlaceBuffer>() {
                        @Override
                        public void onResult(@NonNull PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                LatLng queriedLocation = myPlace.getLatLng();
                                xlat = queriedLocation.latitude;
                                xlon = queriedLocation.longitude;
//                                Log.v("Latitude is", "" + queriedLocation.latitude);
//                                Log.v("Longitude is", "" + queriedLocation.longitude);
                            }
                            places.release();
                        }
                    });
//            snackbar = Snackbar
//                    .make(v, "Unable to detect location,be more specific. \n" +
//                            "Appolo hospitals chennai, Malar Fortis chennai.", Snackbar.LENGTH_LONG);
//
//            snackbar.show();
//            return -1;

        }


        return 0;
    }


    private void bloodgroupsetter() {
        et_bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //material dialog to show all the blood groups

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.chooseblood)
                        .items(R.array.bloodtypes)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_bloodgroup.setTextColor(getResources().getColor(R.color.Red));
                                et_bloodgroup.setText(text);
                                bloodgroup = text.toString();
                            }
                        })
                        .show();


            }
        });

    }

    private void getlatlongfromplacename(String location) {

        xlat = 0.0;
        xlon = 0.0;
        if (Geocoder.isPresent()) {
            try {

                Geocoder gc = new Geocoder(getActivity());
                List <Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                List <LatLng> ll = new ArrayList <>(addresses.size()); // A list to save the coordinates if they are available
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
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            placeId = place.getId();
            //Toast.makeText(c, "ID"+id, Toast.LENGTH_SHORT).show();

            et_location.setText(Html.fromHtml(place.getAddress() + ""));

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

      /*  Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
  */
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

        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();


    }
}