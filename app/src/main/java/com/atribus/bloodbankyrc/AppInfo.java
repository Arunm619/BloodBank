package com.atribus.bloodbankyrc;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.atribus.bloodbankyrc.Model.post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppInfo extends AppCompatActivity implements View.OnClickListener {


    Button Btn_tc, btn_privacy;
    String url_policy = null, tc = null;
    TextView tvic;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        getSupportActionBar().hide();
        Btn_tc = findViewById(R.id.btn_termsandconditions);
        btn_privacy = findViewById(R.id.btn_privacypolicy);
        btn_privacy.setOnClickListener(this);
        Btn_tc.setOnClickListener(this);
       /* btn_privacy.setEnabled(false);
        Btn_tc.setEnabled(false);
       */
        tvic = findViewById(R.id.arun);

        tvic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View parentLayout = findViewById(R.id.pp);
                Snackbar.make(parentLayout, "App developed by Arun Sudharsan.M", Snackbar.LENGTH_LONG)
                        .setAction("Github?", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url = "http://www.github.com/arunm619";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.white))
                        .show();


                return true;

            }
        });
     /*   db = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.privacypolicydburl));
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post post = dataSnapshot.getValue(post.class);
                url_policy = post.getPost_description();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        db = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.tandcdburl));
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post post = dataSnapshot.getValue(post.class);
                tc = post.getPost_description();
                Btn_tc.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

    }


    @Override
    public void onClick(View view) {
        Button b = (Button) view;
        switch (b.getId()) {

            case R.id.btn_privacypolicy:
                //link
                url_policy = "https://www.google.com/policies/privacy/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (!url_policy.startsWith("http://") && !url_policy.startsWith("https://"))
                    url_policy = "http://" + url_policy;
                i.setData(Uri.parse(url_policy));
                startActivity(i);
                break;
            case R.id.btn_termsandconditions:
                //link
                tc = "https://www.google.com/policies/terms/";
                Intent a = new Intent(Intent.ACTION_VIEW);
                if (!tc.startsWith("http://") && !tc.startsWith("https://"))
                    tc = "http://" + tc;
                a.setData(Uri.parse(tc));
                startActivity(a);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        //  startActivity(new Intent(this, Home.class));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
