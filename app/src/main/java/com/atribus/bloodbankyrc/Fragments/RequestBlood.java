package com.atribus.bloodbankyrc.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.Message;
import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RequestBlood extends Fragment {

    FirebaseDatabase database;
    DatabaseReference requestNode;

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
    View v;
    Double xlat = 0.0, xlon = 0.0;

    RelativeLayout rlbloodrequest, rlbloodrequested;
    Button btnedit, btncancel;
    String mobilenumber;
    TextView tvname, tvmobile, tvlocation, tvmessage, tvbloodgroup, tvbloodunits;

    FirebaseDatabase databasenotifications;
    DatabaseReference myRef;


  //  private FusedLocationProviderClient mFusedLocationClient;
   // protected Location mLastLocation;
    Context c;


    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databasenotifications = FirebaseDatabase.getInstance();
        myRef = databasenotifications.getReference("messages");
        c = container.getContext();
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_request_blood, container, false);
      //  mFusedLocationClient = LocationServices.getFusedLocationProviderClient(c);


        // String MY_PREFS_NAME = "MYDB";

        prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        // editor.clear();

        getmobilenumberfromUserObj();
       // getLastLocation();

        database = FirebaseDatabase.getInstance();
        requestNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Request");

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

        Gson g = new Gson();
        String objasstring = prefs.getString("ReqObj", "");


        Request req = g.fromJson(objasstring, Request.class);
        if (req != null) {
            setrequestedscreenon(req);
        }


        bloodgroupsetter();

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
        dorequest();
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

        rlbloodrequested.setVisibility(View.VISIBLE);
        tvmobile.setText(String.valueOf(request.getMobilenumber()));
        tvname.setText(request.getName());
        tvlocation.setText(request.getLocation());
        tvbloodunits.setText(String.valueOf(request.getRequiredunits()) + " units");
        tvbloodgroup.setText(request.getRequiredbloodgroup());
        tvmessage.setText(request.getMessage());

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
                //open that screen with all the data
                //thats all

                // requestobject
                openeditscreen(request);
            }
        });


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });


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

            snackbar = Snackbar
                    .make(v, "Unable to detect location,be more specific. \n" +
                            "Appolo hospitals chennai, Malar Fortis chennai.", Snackbar.LENGTH_LONG);

            snackbar.show();
            return -1;

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