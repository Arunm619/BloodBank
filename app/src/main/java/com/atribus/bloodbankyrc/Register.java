package com.atribus.bloodbankyrc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.atribus.bloodbankyrc.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Register extends AppCompatActivity {


    LinearLayout ll;
    MaterialEditText et_name, et_mobilenumber, et_dob, et_bloodgroup, et_address;
    Button btn_register;
    Date mybirthday;


    String name, mobilenumber, dob, bloodgroup, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Setting up views

        ll = findViewById(R.id.ll);
        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_bloodgroup = findViewById(R.id.et_bloodgroup);
        et_mobilenumber = findViewById(R.id.et_mobilenumber);
        et_dob = findViewById(R.id.et_dob);
        btn_register = findViewById(R.id.btn_register);

        dobsetter();
        bloodgroupsetter();


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = nullcheck();

                if (a != -1)
                    Doregister();
            }
        });


    }

    private int nullcheck() {

        name = et_name.getText().toString().trim();
        bloodgroup = et_bloodgroup.getText().toString().trim();
        mobilenumber = et_mobilenumber.getText().toString().trim();
        address = et_address.getText().toString().trim();
        
        if (name.length() < 1) {
            Snackbar.make(ll, "Enter Name", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (bloodgroup.length() < 1) {
            Snackbar.make(ll, "Choose Blood Group", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (mobilenumber.length() < 1) {
            Snackbar.make(ll, "Enter Mobile Number", Snackbar.LENGTH_LONG).show();
            return -1;
        }


        if (mobilenumber.length() != 10) {
            Snackbar.make(ll, "Check Mobile Number", Snackbar.LENGTH_LONG).show();
            return -1;
        }

        if (address.length() < 1) {
            Snackbar.make(ll, "Enter Complete Address", Snackbar.LENGTH_LONG).show();
            return -1;
        }


        return 0;
    }


    private void dobsetter() {
        et_dob.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View v) {

                //To show current date in the datepicker
                @SuppressLint({"NewApi", "LocalSuppress"}) final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        mybirthday = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();

                        String birthday = "" + selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear + "";
                        dob = birthday;
                        et_dob.setText(birthday);


                        if (validatedate(mybirthday)) {
                            Snackbar.make(ll, birthday + " is not possible", Snackbar.LENGTH_LONG).show();
                            et_dob.setText("");
                            return;
                        }


                        if (validateage(mybirthday)) {
                            Snackbar.make(ll, "Only 18 years old can donate blood.", Snackbar.LENGTH_LONG)
                                    .setAction("Help", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            visitwebsite();
                                        }
                                    })
                                    .show();
                            et_dob.setText("");
                        }


                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();


            }
        });


    }

    public static int getAge(Date date) {

        int age = 0;
        //DateFormat dateFormat = null;
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime((date));
        if (dob.after(now)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1) {
            age--;
        } else if (month1 == month2) {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1) {
                age--;
            }
        }
        return age;
    }

    private boolean validateage(Date date) {

        return getAge(date) < 18;
    }

    private boolean validatedate(Date date) {

        assert date != null;
        return !date.before(new Date());


    }


    private void bloodgroupsetter() {
        et_bloodgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //material dialog to show all the blood groups

                new MaterialDialog.Builder(Register.this)
                        .title(R.string.chooseblood)
                        .items(R.array.bloodtypes)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                et_bloodgroup.setTextColor(getResources().getColor(R.color.Red));
                                et_bloodgroup.setText(text);
                            }
                        })
                        .show();


            }
        });

    }

    private void visitwebsite() {
        String url = getString(R.string.donorguideliines);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }


    private void Doregister() {

        /*  String name;
        int age;
        Date dateofbirth;
        Date dateofregistration;
        String bloodgroup;
        String address;
        Long lattitude,longitude;
        Long mobilenumber;*/

        Date temp = parsedate(dob);

        User user = new User();
        user.setName(name);
        user.setAddress(address);
        user.setMobilenumber(Long.valueOf(mobilenumber));
        user.setBloodgroup(bloodgroup);
        user.setDateofbirth(temp);
        user.setDateofregistration(new Date());
        user.setAge(getAge(temp));


        Toast.makeText(this, "" + user.toString(), Toast.LENGTH_SHORT).show();


    }


    @SuppressLint("SimpleDateFormat")
    private Date parsedate(String birthday) {
        DateFormat df;
        df = new SimpleDateFormat("MM/dd/yyyy");
        Date Date = null;
        try {
            Date = df.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Date;
    }
}
