package com.atribus.bloodbankyrc.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Model.BloodDonations;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.RecentBloodDonationsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import cn.iwgang.countdownview.CountdownView;

import static android.content.Context.MODE_PRIVATE;


public class HelperFragment extends Fragment {

    //blooddonation
    String hostpitalname;
    String hospitalocation;
    //String UUID;
    //Long mobile;
    String date;

    private Date mydonationdate;
    private View ll;

    FirebaseUser currentUser;
    String MY_PREFS_NAME = "MYDB";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public HelperFragment() {
        // Required empty public constructor
    }

    DatabaseReference DonationsNode;
    MaterialEditText et_hospitalname, et_location, et_dodonation;
    Button btn_submit, btnshowdetails;
    Button btn_donatedbloodq;
    CardView cv_donatedbloodquestion, cv_formdonationdetails, cv_countdowntonextdonation;
    TextView tv_daysuntilnextdonation;
    CountdownView cdvtimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_helper, container, false);
        ll = v.findViewById(R.id.RLDonatedDetails);
        et_dodonation = v.findViewById(R.id.et_dateofdonation);
        et_hospitalname = v.findViewById(R.id.et_Hospitalname);
        et_location = v.findViewById(R.id.et_location);

        cv_countdowntonextdonation = v.findViewById(R.id.cv_countdowntonextdonation);
        cv_formdonationdetails = v.findViewById(R.id.cvformdonationdetails);
        cv_donatedbloodquestion = v.findViewById(R.id.cv_donatedblood);
        tv_daysuntilnextdonation = v.findViewById(R.id.daysuntilnextdonation);
        btn_submit = v.findViewById(R.id.btnsubmit);
        btn_donatedbloodq = v.findViewById(R.id.btn_donatedbloodq);
        btnshowdetails = v.findViewById(R.id.btnshowdetails);
        cdvtimer = v.findViewById(R.id.cv_countdownViewTest4);
        cdvtimer.setVisibility(View.GONE);

        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();

        String dateoflastdonation = prefs.getString(getString(R.string.keylastdonateddate), null);
        // Toast.makeText(getActivity(), "STORED :" + dateoflastdonation, Toast.LENGTH_SHORT).show();
        btn_donatedbloodq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv_donatedbloodquestion.setVisibility(View.GONE);
                cv_formdonationdetails.setVisibility(View.VISIBLE);
            }
        });

        btnshowdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RecentBloodDonationsActivity.class));
            }
        });
        if (dateoflastdonation != null && dateoflastdonation.length() != 0) {
            cv_countdowntonextdonation.setVisibility(View.VISIBLE);
            cv_donatedbloodquestion.setVisibility(View.GONE);
            cv_formdonationdetails.setVisibility(View.GONE);


            calculatedays(dateoflastdonation);
            tv_daysuntilnextdonation.setText(String.format("%s %s", getString(R.string.recentDonation), dateoflastdonation));


        } else {
            cv_donatedbloodquestion.setVisibility(View.VISIBLE);


        }

        //getting firebase auth instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DonationsNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Donations");
        DatabaseReference BloodNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/Blood");

        dobsetter();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int a = nullcheck();
                if (a != -1)
                    dosave();
            }
        });


        return v;
    }

    private void calculatedays(String dateoflastdonation) {


        Date donationdate = parsedate(dateoflastdonation);

        Date today = new Date();

        //today-6 weeks = donation date

        //Date today=new Date();
        //42 days 6 weeks

        // Date today=new Date();
        long ltime;
        ltime = donationdate.getTime() + 42 * 24 * 60 * 60 * 1000;
        Date fullyrecovereddate = new Date(ltime);
        // Toast.makeText(getActivity(), "RECOVERED"+fullyrecovereddate.toString(), Toast.LENGTH_SHORT).show();


        long daysleft = getDateDiff(fullyrecovereddate, today, TimeUnit.DAYS);

        if (daysleft > 0) {
            Toast.makeText(getActivity(), "Days left :" + daysleft, Toast.LENGTH_SHORT).show();

        } else {
            cv_donatedbloodquestion.setVisibility(View.VISIBLE);
            cv_formdonationdetails.setVisibility(View.GONE);
            cv_countdowntonextdonation.setVisibility(View.GONE);

        }


    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private int computediffdays(Date today, Date fullyrecovereddate) {
        int a = today.compareTo(fullyrecovereddate);

        Calendar todaycal = Calendar.getInstance();
        todaycal.setTime(today);
        Calendar fullyreccal = Calendar.getInstance();
        fullyreccal.setTime(fullyrecovereddate);


        //long ans[] = new long[2];
        if (a != 0) {
            long msDiff = fullyreccal.getTimeInMillis() - todaycal.getTimeInMillis();
            Long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);

            return daysDiff.intValue();
        } else {
            //today is your birthday
            return 0;

        }


    }

    @SuppressLint("SimpleDateFormat")
    private Date parsedate(String birthday) {
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        Date Date = null;
        try {
            Date = df.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Date;
    }

    private void dosave() {

        BloodDonations donations = new BloodDonations();
        donations.setDate(date);
        donations.setHospitalocation(hospitalocation);
        donations.setHostpitalname(hostpitalname);
        donations.setUuid(currentUser.getUid());

        //stored in firebase
        DonationsNode.child(currentUser.getUid()).push().setValue(donations);


        //stored in local
        // date as lastdate

        editor.putString(getString(R.string.keylastdonateddate), date);
        editor.commit();

        Toast.makeText(getActivity(), "DATE :" + date, Toast.LENGTH_SHORT).show();
        cv_formdonationdetails.setVisibility(View.GONE);
        cv_countdowntonextdonation.setVisibility(View.VISIBLE);

    }

    private int nullcheck() {
        hospitalocation = et_location.getText().toString().trim();
        hostpitalname = et_hospitalname.getText().toString().trim();
        date = et_dodonation.getText().toString().trim();

        if (hostpitalname.length() < 1) {
            Snackbar.make(ll, "Enter Hospital Name", Snackbar.LENGTH_LONG).show();
            return -1;
        }
        if (hospitalocation.length() < 1) {
            Snackbar.make(ll, "Enter Location", Snackbar.LENGTH_LONG).show();
            return -1;
        }
        if (date.length() < 1) {
            Snackbar.make(ll, "Please select Date", Snackbar.LENGTH_LONG).show();
            return -1;
        }


        return 0;
    }


    private void dobsetter() {
        et_dodonation.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View v) {

                //To show current date in the datepicker
                @SuppressLint({"NewApi", "LocalSuppress"}) final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        mydonationdate = new GregorianCalendar(selectedyear, selectedmonth, selectedday).getTime();

                        String ddate = "" + selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear + "";
                        //dob = birthday;
                        et_dodonation.setText(ddate);


                        if (validatedate(mydonationdate)) {
                            Snackbar.make(ll, ddate + " is not possible", Snackbar.LENGTH_LONG).show();
                            et_dodonation.setText("");
                            return;
                        }


                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();


            }
        });


    }

    private boolean validatedate(Date date) {

        assert date != null;
        return !date.before(new Date());


    }


}
