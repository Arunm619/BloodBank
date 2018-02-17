package com.atribus.bloodbankyrc.AdminPackage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.atribus.bloodbankyrc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboard extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");
    DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");
    DatabaseReference PostsNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/PostDetails");
    DatabaseReference RequestNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Request");
    DatabaseReference SuccessRequest = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/SuccessRequest");

    TextView tvtotalnumberofposts,
            tvtotalnumberofusers,
            tvtotalnumberofrequestspending,
            tvtotalnumberofrequestssuccess;
    TextView tvCountforABpositive, tvCountforApositive, tvCountforBpositive, tvCountforOpositive;
    TextView tvCountforABnegative, tvCountforAnegative, tvCountforBnegative, tvCountforOnegative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        getSupportActionBar().setTitle("DashBoard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        tvtotalnumberofposts = findViewById(R.id.tvtotalnumberofposts);
        tvtotalnumberofrequestspending = findViewById(R.id.tvtotalnumberofrequestspending);
        tvtotalnumberofrequestssuccess = findViewById(R.id.tvtotalnumberofrequestssuccess);
        tvtotalnumberofusers = findViewById(R.id.tvtotalnumberofusers);

        tvCountforABnegative = findViewById(R.id.tvCountforABnegative);
        tvCountforAnegative = findViewById(R.id.tvCountforAnegative);
        tvCountforBnegative = findViewById(R.id.tvCountforBnegative);
        tvCountforOnegative = findViewById(R.id.tvCountforOnegative);

        tvCountforABpositive = findViewById(R.id.tvCountforABpositive);
        tvCountforApositive = findViewById(R.id.tvCountforApositive);
        tvCountforBpositive = findViewById(R.id.tvCountforBpositive);
        tvCountforOpositive = findViewById(R.id.tvCountforOpositive);


        //Setting Number of Posts Count
        PostsNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvtotalnumberofposts.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Setting Pending Requests Count
        RequestNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvtotalnumberofrequestspending.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Setting Number of Successful Requets
        SuccessRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvtotalnumberofrequestssuccess.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Setting Number of Users Count
        UsersNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvtotalnumberofusers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Setting blood Count

        //AB+
        BloodNode.child("AB+").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforABpositive.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //AB-
        BloodNode.child("AB-").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforABnegative.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //A+
        BloodNode.child("A+").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforApositive.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //A-
        BloodNode.child("A-").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforAnegative.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //B+
        BloodNode.child("B+").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforBpositive.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //B-
        BloodNode.child("B-").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforBnegative.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //O+
        BloodNode.child("O+").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforOpositive.setText(String.valueOf(dataSnapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //O-
        BloodNode.child("O-").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCountforOnegative.setText(String.valueOf(dataSnapshot.getChildrenCount()));

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
