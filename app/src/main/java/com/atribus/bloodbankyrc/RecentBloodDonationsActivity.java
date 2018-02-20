package com.atribus.bloodbankyrc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecentBloodDonationsActivity extends AppCompatActivity {
    FirebaseUser currentUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference DonationsNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Donations");
    TextView tvnumberoftimes;
    Button btn_share;
    int countvarforshare = 0;
    PieChart pieChart;
    DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");
    DatabaseReference UsersNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Users");

    String userblood;
    String MY_PREFS_NAME = "MYDB";

    long ABpositive = 0, ABnegative = 0, Apositive = 0, Anegative = 0, Bpositive = 0, Bnegative = 0, Opositive = 0, Onegative = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_blood_donations);
        getSupportActionBar().setTitle("Achievements");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        userblood = prefs.getString("blood", "else");


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
        pieChart = findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);


        DonationsNode.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                if (count < 1) {
                    tvnumberoftimes.setText(R.string.YettoBegin);
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

        BloodNode.child(userblood).addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                makepiechart(count);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void makepiechart(long count) {
        ArrayList <Entry> yvalues = new ArrayList <>();

        yvalues.add(new Entry(count, 0));
/*
        yvalues.add(new Entry(ABpositive, 0));
        yvalues.add(new Entry(ABnegative, 1));
        yvalues.add(new Entry(Apositive, 2));
        yvalues.add(new Entry(Anegative, 3));
        yvalues.add(new Entry(Bpositive, 4));
        yvalues.add(new Entry(Bnegative, 5));
        yvalues.add(new Entry(Opositive, 6));
        yvalues.add(new Entry(Onegative, 7));*/
        ArrayList <String> xVals = new ArrayList <>();

        xVals.add(userblood);
        /*xVals.add("AB-");
        xVals.add("A+");
        xVals.add("A-");
        xVals.add("B+");
        xVals.add("B-");
        xVals.add("O+");
        xVals.add("O-");
*/
        PieDataSet dataSet = new PieDataSet(yvalues, "Total No of Users of type " + userblood);

        final PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());

        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(30f);


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
