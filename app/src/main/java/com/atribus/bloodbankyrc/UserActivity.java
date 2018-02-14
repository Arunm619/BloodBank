package com.atribus.bloodbankyrc;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Adapters.ViewpagerAdapter;
import com.atribus.bloodbankyrc.Fragments.DonateBlood;
import com.atribus.bloodbankyrc.Fragments.HelperFragment;
import com.atribus.bloodbankyrc.Fragments.HomeFragment;
import com.atribus.bloodbankyrc.Fragments.RequestBlood;
import com.atribus.bloodbankyrc.Model.User;
import com.google.gson.Gson;

public class UserActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    //AppBarLayout appBarLayout;

    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;

    String dataTitle, dataMessage;


    FloatingActionButton fab;
    private static final int PAGE_HOME = 0;
    private static final int PAGE_DONATE = 1;

    private static final int PAGE_REQUEST = 2;
    private static final int PAGE_FACTS = 3;

    private int[] tabIcons = {
            R.drawable.ic_address,
            R.drawable.ic_blood,
            R.drawable.ic_dob,
            R.drawable.ic_location
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setTitle("Blood Bank");
        getSupportActionBar().setElevation(0);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        // Handle possible data accompanying notification message.
        if (getIntent().getExtras() != null) {
            viewPager.setCurrentItem(PAGE_DONATE);

            Toast.makeText(this, "sibaji", Toast.LENGTH_SHORT).show();
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("title")) {
                    dataTitle = (String) getIntent().getExtras().get(key);
                }
                if (key.equals("message")) {
                    dataMessage = (String) getIntent().getExtras().get(key);
                }
            }

            showAlertDialog();

        }


        Gson gson = new Gson();
        String json = prefs.getString("UserObj", "");

        User obj = gson.fromJson(json, User.class);
        if (obj == null) {
            startActivity(new Intent(this, Register.class));

            finish();
        }

        fab = findViewById(R.id.fab);


        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        //  appBarLayout = findViewById(R.id.appbarid);

        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        adapter.Addfragment(new HomeFragment(), "Home");
        adapter.Addfragment(new DonateBlood(), "Donate");
        adapter.Addfragment(new RequestBlood(), "Request");
        adapter.Addfragment(new HelperFragment(), "Blood ");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // setuptabicons();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {

                    case PAGE_HOME:
                        fab.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager.setCurrentItem(PAGE_REQUEST, true);
                            }
                        });
                        break;
                    case PAGE_REQUEST:
                        fab.setVisibility(View.GONE);
                        break;

                    case PAGE_DONATE:
                        fab.setVisibility(View.GONE);
                        break;
                    case PAGE_FACTS:
                        fab.setVisibility(View.GONE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(UserActivity.this, "SHow details", Toast.LENGTH_SHORT).show();
                            }
                        });
                }

            }

        });

    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage("Required Blood Group: " + dataTitle + "\n" + "Contact Number: " + dataMessage);
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    private void setuptabicons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(UserActivity.this, Settings.class));

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
