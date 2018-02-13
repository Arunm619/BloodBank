package com.atribus.bloodbankyrc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecentBloodDonationsActivity extends AppCompatActivity {
    FirebaseUser currentUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference DonationsNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Donations");
    TextView tvnumberoftimes;
    Button btn_share;
    int countvarforshare = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_blood_donations);
        getSupportActionBar().setTitle("Achievements");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getting firebase auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        tvnumberoftimes = findViewById(R.id.tvNOFTIMESDONATED);
        btn_share = findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharemessage();
            }
        });

        DonationsNode.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                if (count < 1) {
                    tvnumberoftimes.setText("Yet to begin.");
                    btn_share.setEnabled(false);
                } else {
                    tvnumberoftimes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    btn_share.setEnabled(true);
                    countvarforshare = (int) count;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharemessage() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey guys I have saved " + countvarforshare + " people using this app! ");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
