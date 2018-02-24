package com.atribus.bloodbankyrc;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AppInfo extends AppCompatActivity {


    Button Btn_tc, btn_privacy, btn_fb, btn_whatsapp, btn_github;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        if (getSupportActionBar()!=null)
        getSupportActionBar().hide();
        Btn_tc = findViewById(R.id.btn_termsandconditions);
        btn_privacy = findViewById(R.id.btn_privacypolicy);
        btn_fb = findViewById(R.id.btn_fb);
        btn_github = findViewById(R.id.btn_github);
        btn_whatsapp = findViewById(R.id.btn_whatsapp);

        View parentLayout = findViewById(R.id.pp);

//        String url = "http://www.github.com/arunm619";
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(url));
//        startActivity(i);


        Btn_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLongDisclaimerwith();
            }
        });

        btn_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.bloodbankwebsite);
                openchrometab(url);
            }
        });

        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(String.valueOf("91" + "9940245619")) + "@s.whatsapp.net");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    Toast.makeText(AppInfo.this, "Please Install Whatsapp On your phone to whatsapp Admin!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openchrometab(getString(R.string.githublink));
            }
        });

        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openchrometab(getString(R.string.fblink));

            }
        });
    }

    private void callLongDisclaimerwith() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.TermsandPolicies);
        alertDialog.setMessage(R.string.TermsandPoliciesLongtext);
        alertDialog.setPositiveButton("OK", null);
        alertDialog.setNegativeButton("See More", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "http://www.blooddonation.com/termsandpolicies";
                openchrometab(url);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();


    }

    private void openchrometab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setToolbarColor(getColor(R.color.colorPrimary));
        }
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
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

}
