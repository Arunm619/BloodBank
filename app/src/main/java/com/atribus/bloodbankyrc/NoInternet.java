package com.atribus.bloodbankyrc;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NoInternet extends AppCompatActivity {
    Button btn_checkinternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        btn_checkinternet = findViewById(R.id.btn_checkinternet);
        btn_checkinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOnline();
            }
        });
    }

    public void isOnline() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getActiveNetworkInfo();
        }

        if (info == null) {
            return;
        } else {
            Intent i = new Intent(NoInternet.this, UserActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
    }
}
