package com.atribus.bloodbankyrc.AdminPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.atribus.bloodbankyrc.R;

public class ADMIN extends AppCompatActivity {
    Button btn_search, btn_addapost, btn_checkdetails, btn_appdashboard;
    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admin Panel ");

        rl = findViewById(R.id.rl);
        btn_search = findViewById(R.id.btn_search);
        btn_addapost = findViewById(R.id.btn_addapost);
        btn_checkdetails = findViewById(R.id.btn_checkdetails);
        btn_appdashboard = findViewById(R.id.btn_appdashboard);

        //seeting all buttons hidden
        btn_addapost.setEnabled(false);
        btn_search.setEnabled(false);
        btn_checkdetails.setEnabled(false);
        btn_appdashboard.setEnabled(false);

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

        btn_appdashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ADMIN.this, AdminDashboard.class));
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setMessage("ADMIN APP LOCKED");
        alert.setTitle("Enter password to unlock");

        alert.setView(edittext);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String password = edittext.getText().toString();
                if (password.equals("123")) {

                    //Toast.makeText(ADMIN.this, "Welcome Back, Admin!", Toast.LENGTH_SHORT).show();

                    Snackbar.make(rl, "Welcome Back, Admin!", Snackbar.LENGTH_LONG).show();
                    btn_addapost.setEnabled(true);
                    btn_checkdetails.setEnabled(true);
                    btn_search.setEnabled(true);
                    btn_appdashboard.setEnabled(true);
                } else {
                    Snackbar.make(rl, "Wrong Password! Closing App!", Snackbar.LENGTH_LONG).show();

                    //Toast.makeText(ADMIN.this, "Wrong Password! Closing App", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            finish();

                        }
                    }, 2000);

                }
            }
        });

        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });

        alert.setCancelable(false).show();


    }
}
