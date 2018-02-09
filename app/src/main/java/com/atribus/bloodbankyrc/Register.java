package com.atribus.bloodbankyrc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Register extends AppCompatActivity {

    private String MY_PREFS_NAME = "MYDB";
    SharedPreferences.Editor editor;

    LinearLayout ll;
    MaterialEditText et_name, et_mobilenumber, et_dob, et_bloodgroup, et_address;
    Button btn_register;
    Date mybirthday;
    Double mlat = 0.0, mlon = 0.0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");
    DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");

    String name, mobilenumber, dob, bloodgroup, address;

    FirebaseUser currentUser;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private int REQUEST_PERMISSIONS_REQUEST_CODE = 101;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getting firebase auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        ll = findViewById(R.id.ll);
        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_bloodgroup = findViewById(R.id.et_bloodgroup);
        et_mobilenumber = findViewById(R.id.et_mobilenumber);
        et_dob = findViewById(R.id.et_dob);
        btn_register = findViewById(R.id.btn_register);

        dobsetter();
        bloodgroupsetter();


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = nullcheck();

                if (a != -1)
                    Doregister();
            }
        });


    }

    private int nullcheck() {

        name = et_name.getText().toString().trim();
        bloodgroup = et_bloodgroup.getText().toString().trim();
        mobilenumber = et_mobilenumber.getText().toString().trim();
        address = et_address.getText().toString().trim();

        if (name.length() < 1) {
            Snackbar.make(ll, "Enter Name", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (bloodgroup.length() < 1) {
            Snackbar.make(ll, "Choose Blood Group", Snackbar.LENGTH_LONG).show();
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

        if (address.length() < 1) {
            Snackbar.make(ll, "Enter Complete Address", Snackbar.LENGTH_LONG).show();
            return -1;
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
        editor.commit();

        Toast.makeText(this, "Successfully Stored", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(Register.this, Home.class));
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

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
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
                            Toast.makeText(Register.this, "" + mLastLocation.toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            //        Log.w(TAG, "getLastLocation:exception", task.getException());
                            //       showSnackbar(getString(R.string.no_location_detected));
                            Toast.makeText(Register.this, "No location detected", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(Register.this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            // Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            requestPermissions();
                        }
                    });

        } else {
            // Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                //       Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}
