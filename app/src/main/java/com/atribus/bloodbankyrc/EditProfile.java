package com.atribus.bloodbankyrc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class EditProfile extends AppCompatActivity {

    MaterialEditText et_name, et_mobilenumber, et_dob, et_bloodgroup, et_address, et_gender;

    String name, mobilenumber, dob, bloodgroup, address, gender;
    LinearLayout ll;
    Button btn_update;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");
    DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");
    FirebaseUser currentUser;

    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Date mybirthday;
    private double mlat = 0.0, mlon = 0.0;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        ll = findViewById(R.id.ll);
        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_bloodgroup = findViewById(R.id.et_bloodgroup);
        et_mobilenumber = findViewById(R.id.et_mobilenumber);
        et_dob = findViewById(R.id.et_dob);
        et_gender = findViewById(R.id.et_gender);
        btn_update = findViewById(R.id.btn_update);

        //getting firebase auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();


        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj != null) {
            mobilenumber = String.valueOf(obj.getMobilenumber());
            name = obj.getName();
            dob = obj.getDateofbirth();
            bloodgroup = obj.getBloodgroup();
            address = obj.getAddress();
            gender = obj.getGender();

            et_name.setText(name);
            et_address.setText(address);
            et_bloodgroup.setText(bloodgroup);
            et_mobilenumber.setText(mobilenumber);
            et_dob.setText(dob);
            et_gender.setText(gender);


            //  Toast.makeText(getActivity(), "Mobile :" + mobilenumber, Toast.LENGTH_SHORT).show();
        }

        dobsetter();
        bloodgroupsetter();
        gendersetter();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = nullcheck();

                if (a != -1)
                    Doregister();
            }
        });


        //setting mobile number from firebase user

    }

    private int nullcheck() {

        name = et_name.getText().toString().trim();
        bloodgroup = et_bloodgroup.getText().toString().trim();
        mobilenumber = et_mobilenumber.getText().toString().trim();
        address = et_address.getText().toString().trim();
        gender = et_gender.getText().toString().trim();

        if (name.length() < 1) {
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

                DatePickerDialog mDatePicker = new DatePickerDialog(EditProfile.this, new DatePickerDialog.OnDateSetListener() {

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

        Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show();

        // startActivity(new Intent(EditProfile.this, UserActivity.class));
        //finish();

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @SuppressLint("SimpleDateFormat")
    private Date parsedate(String birthday) {
        DateFormat df;
        df = new SimpleDateFormat("MM/dd/yyyy");
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
        simpleDate = new SimpleDateFormat("MM/dd/yyyy");

        return simpleDate.format(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

        return getAge(date) <= 18;
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

                new MaterialDialog.Builder(EditProfile.this)
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

                new MaterialDialog.Builder(EditProfile.this)
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
                            Toast.makeText(EditProfile.this, "" + mLastLocation.toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(EditProfile.this, "Failed to track location", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }


}
