package com.atribus.bloodbankyrc.AdminPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.atribus.bloodbankyrc.R;

public class ADMIN extends AppCompatActivity {
    Button btn_search, btn_addapost,btn_checkdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admin Panel ");
        btn_search = findViewById(R.id.btn_search);
        btn_addapost = findViewById(R.id.btn_addapost);
        btn_checkdetails=findViewById(R.id.btn_checkdetails);

        btn_checkdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ADMIN.this, AdminCheckDetails.class));
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ADMIN.this, AdminMain.class));
            }
        });
        btn_addapost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ADMIN.this, AdminAddPost.class));

            }
        });
    }
}
