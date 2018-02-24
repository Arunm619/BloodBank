package com.atribus.bloodbankyrc.AdminPackage;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class AdminCheckDetails extends AppCompatActivity {
    MaterialEditText et_mobilenumber;
    Button btn_search, btn_sendmsg, btn_call;
    String mobilenumber = null;

    FirebaseDatabase database;
    DatabaseReference UserNode;
    ProgressDialog pd;
    CardView cv_adminpanelResult, cv_adminpanelsearch;
    TextView tvName, tvmobile, tvblood, tvlocation, tvage, tvgender, tvdob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        database = FirebaseDatabase.getInstance();
        UserNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");

        et_mobilenumber = findViewById(R.id.et_mobilenumber);
        btn_search = findViewById(R.id.btn_search);
        btn_sendmsg = findViewById(R.id.btn_sendmsg);
        btn_call = findViewById(R.id.btn_call);
        cv_adminpanelResult = findViewById(R.id.cv_adminpanelResult);

        cv_adminpanelsearch = findViewById(R.id.cv_adminpanelsearch);
        cv_adminpanelsearch.setVisibility(View.VISIBLE);
        cv_adminpanelResult.setVisibility(View.GONE);

        tvName = findViewById(R.id.tvName);
        tvmobile = findViewById(R.id.tvmobile);
        tvblood = findViewById(R.id.tvblood);
        tvlocation = findViewById(R.id.tvlocation);
        tvage = findViewById(R.id.tvage);
        tvgender = findViewById(R.id.tvgender);
        tvdob = findViewById(R.id.tvdob);


        pd = new ProgressDialog(AdminCheckDetails.this);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobilenumber = et_mobilenumber.getText().toString().trim();
                if (TextUtils.isEmpty(mobilenumber)) {
                    Toast.makeText(AdminCheckDetails.this, "Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mobilenumber.length() != 10) {
                    Toast.makeText(AdminCheckDetails.this, "Check Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }


                //start loading the pd
                pd.setMessage("Searching User with Mobile Number : " + mobilenumber);
                pd.show();
                search(mobilenumber);
            }
        });


    }

    private void search(final String mobilenumber) {

        UserNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null) {
                        if (String.valueOf(user.getMobilenumber()).equals(mobilenumber)) {
                            getperson(ds.getKey());
                            pd.dismiss();
                            return;
                        }
                    }

                }
                pd.dismiss();
                Toast.makeText(AdminCheckDetails.this, "No Users found With Mobile Number " + mobilenumber, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getperson(String key) {
        UserNode.child(key).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User student = dataSnapshot.getValue(User.class);

                cv_adminpanelsearch.setVisibility(View.GONE);
                cv_adminpanelResult.setVisibility(View.VISIBLE);
                if (student != null) {
                    tvName.setText(student.getName());
                    tvage.setText(student.getAge() + " years ");
                    tvblood.setText(student.getBloodgroup());
                    tvdob.setText(student.getDateofbirth());
                    tvgender.setText(student.getGender());
                    tvmobile.setText(String.valueOf(student.getMobilenumber()));
                    tvlocation.setText(student.getAddress());
                }
                btn_sendmsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent sendIntent = new Intent("android.intent.action.MAIN");
                            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(String.valueOf("91" + student.getMobilenumber())) + "@s.whatsapp.net");
                            startActivity(sendIntent);
                        } catch (Exception e) {
                            Toast.makeText(AdminCheckDetails.this, "Please Install Whatsapp On your phone, Admin!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

                btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = mobilenumber;
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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


}




  /*void  dummy(){
      Query query = UserNode.child("mobilenumber")*//*.orderByChild(getString(R.string.dbfieldmobilenumber))*//*.equalTo(mobilenumber);
      query.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

              for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                  String key = childSnapshot.getKey();
                  getperson(key);


                  // Log.i(TAG,key);
              }

              Toast.makeText(AdminCheckDetails.this, "" + dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
              //    Toast.makeText(AdminCheckDetails.this, "No Users found With Mobile Number " + mobilenumber, Toast.LENGTH_SHORT).show();
              //    Toast.makeText(ScanQRActivity.this, key, Toast.LENGTH_SHORT).show();


          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

  }*/