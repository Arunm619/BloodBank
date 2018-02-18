package com.atribus.bloodbankyrc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.Utils.PlaceArrayAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Register extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    private static final String LOG_TAG = "Arun checks";
    private static final int GOOGLE_API_CLIENT_ID = 10;
    private AutoCompleteTextView mAutocompleteTextView;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));


    SharedPreferences.Editor editor;

    LinearLayout ll;
    MaterialEditText et_name, et_mobilenumber, et_dob, et_bloodgroup, et_address, et_gender;
    Button btn_register;
    Date mybirthday;
    Double mlat = 0.0, mlon = 0.0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");
    DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");

    String name, mobilenumber, dob, bloodgroup, address, gender;

    FirebaseUser currentUser;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private int REQUEST_PERMISSIONS_REQUEST_CODE = 101;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register New Profile");

        //getting firebase auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        String MY_PREFS_NAME = "MYDB";
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
        // Toast.makeText(this, "Registered to Notifications...", Toast.LENGTH_SHORT).show();
        ll = findViewById(R.id.ll);
        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_bloodgroup = findViewById(R.id.et_bloodgroup);
        et_mobilenumber = findViewById(R.id.et_mobilenumber);
        et_dob = findViewById(R.id.et_dob);
        et_gender = findViewById(R.id.et_gender);
        btn_register = findViewById(R.id.btn_register);

        //setting mobile number from firebase user
        if (currentUser != null)
            et_mobilenumber.setText(currentUser.getPhoneNumber());

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User u = dataSnapshot.getValue(User.class);
                    et_name.setText(u.getName());
                    et_mobilenumber.setText(String.valueOf(u.getMobilenumber()));
                    //et_dob.setText(u.getDateofbirth());
                    et_gender.setText(u.getGender());

                    et_bloodgroup.setText(u.getBloodgroup());
                    et_address.setText(u.getAddress());
                    mAutocompleteTextView.setText(u.getAddress());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dobsetter();
        bloodgroupsetter();
        gendersetter();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = nullcheck();

                if (a != -1)
                    Doregister();
            }
        });


        //Autocomplete
        mGoogleApiClient = new GoogleApiClient.Builder(Register.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(Register.this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = findViewById(R.id
                .et_autocomplete);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_INDIA, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);


    }

    private int nullcheck() {

        name = et_name.getText().toString().trim();
        bloodgroup = et_bloodgroup.getText().toString().trim();
        mobilenumber = et_mobilenumber.getText().toString().trim();
        address = et_address.getText().toString().trim();
        gender = et_gender.getText().toString().trim();
        if (TextUtils.isDigitsOnly(name)) {
            Snackbar.make(ll, "Enter Name ", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (TextUtils.isEmpty(name)) {
            Snackbar.make(ll, "Enter Name", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (mobilenumber.length() < 1) {
            Snackbar.make(ll, "Enter Mobile Number", Snackbar.LENGTH_LONG).show();
            return -1;
        }


        if (mobilenumber.length() != 10) {
            Snackbar.make(ll, "Check Mobile Number", Snackbar.LENGTH_LONG).show();
            return -1;
        }
        if (TextUtils.isEmpty(dob)) {
            Snackbar.make(ll, "Choose DOB", Snackbar.LENGTH_LONG).show();
            return -1;

        }

        if (bloodgroup.length() < 1) {
            Snackbar.make(ll, "Choose Blood Group", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (TextUtils.isEmpty(gender)) {
            Snackbar.make(ll, "Choose Gender", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (address.length() < 1) {
            Snackbar.make(ll, "Enter Complete Address", Snackbar.LENGTH_LONG).show();
            return -1;
        }


        if (mlon == 0.0 || mlat == 0.0) {
            String temploc = et_address.getText().toString();
            if (!TextUtils.isEmpty(temploc))
                getlatlongfromplacename(temploc);


        }


        return 0;
    }


    private void dobsetter() {
        et_dob.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View v) {

                //To show current date in the datepicker
                @SuppressLint({"NewApi", "LocalSuppress"}) final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        mybirthday = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();

                        String birthday = "" + selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear + "";
                        dob = birthday;
                        et_dob.setText(birthday);


                        if (validatedate(mybirthday)) {
                            Snackbar.make(ll, birthday + " is not possible", Snackbar.LENGTH_LONG).show();
                            et_dob.setText("");
                            return;
                        }


                        if (validateage(mybirthday)) {
                            Snackbar.make(ll, "Only 18 years old can donate blood.", Snackbar.LENGTH_LONG)
                                    .setAction("Help", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            visitwebsite();
                                        }
                                    })
                                    .show();
                            et_dob.setText("");
                        }


                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();


            }
        });


    }


    private void Doregister() {


        Date parsedate = parsedate(dob);

        User user = new User();

        user.setName(name);
        user.setAddress(address);
        user.setMobilenumber(Long.valueOf(mobilenumber));
        user.setBloodgroup(bloodgroup);
        user.setDateofbirth(dob);
        user.setDateofregistration(parseDatetostring(new Date()));
        user.setAge(getAge(parsedate));
        user.setEmail(currentUser.getEmail());
        user.setGender(gender);
        user.setLattitude(mlat);
        user.setLongitude(mlon);

        //generating USER ID
        String UUID = currentUser.getUid();

        //Adding to Users Node in DB
        UsersNode.child(UUID).setValue(user);

        //Adding to Blood Node in DB
        BloodNode.child(user.getBloodgroup()).child(UUID).setValue(user);

        //storing offline copy of UserObject in sharedpred under MYDB
        Gson gson = new Gson();
        String userobjectasstring = gson.toJson(user);
        editor.putString("UserObj", userobjectasstring);
        editor.putString("blood", user.getBloodgroup());

        editor.apply();

        Toast.makeText(this, "Successfully Stored", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(Register.this, UserActivity.class));
        finish();

    }


    @SuppressLint("SimpleDateFormat")
    private Date parsedate(String birthday) {
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        Date Date = null;
        try {
            Date = df.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Date;
    }

    @SuppressLint("SimpleDateFormat")
    private String parseDatetostring(Date date) {
        SimpleDateFormat simpleDate;
        simpleDate = new SimpleDateFormat("dd/MM/yyyy");

        return simpleDate.format(date);
    }

    private void visitwebsite() {
        String url = getString(R.string.donorguideliines);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }


    public static int getAge(Date date) {

        int age = 0;
        //DateFormat dateFormat = null;
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime((date));
        if (dob.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return age;
    }

    private boolean validateage(Date date) {

        return getAge(date) < 18;
    }

    private boolean validatedate(Date date) {

        assert date != null;
        return !date.before(new Date());


    }


    private void bloodgroupsetter() {
        et_bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //material dialog to show all the blood groups

                new MaterialDialog.Builder(Register.this)
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

    private void gendersetter() {
        et_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //material dialog to show all the blood groups

                new MaterialDialog.Builder(Register.this)
                        .title(R.string.choosegender)
                        .items(R.array.gender)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_gender.setText(text);
                            }
                        })
                        .show();


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();


        getLastLocation();

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener <Location>() {
                    @Override
                    public void onComplete(@NonNull Task <Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            mlat = mLastLocation.getLatitude();
                            mlon = mLastLocation.getLongitude();
                            //  Toast.makeText(Register.this, "" + mLastLocation.toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            //Toast.makeText(Register.this, "Failed to track location", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    private void getlatlongfromplacename(String location) {

        mlat = 0.0;
        mlon = 0.0;
        if (Geocoder.isPresent()) {
            try {
                ;
                Geocoder gc = new Geocoder(this);
                List <Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                List <LatLng> ll = new ArrayList <LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        mlat = a.getLatitude();
                        mlon = a.getLongitude();
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
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            et_address.setText(Html.fromHtml(place.getAddress() + ""));

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


}
