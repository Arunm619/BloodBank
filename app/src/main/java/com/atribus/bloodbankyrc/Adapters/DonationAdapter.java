package com.atribus.bloodbankyrc.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atribus.bloodbankyrc.Model.Request;
import com.atribus.bloodbankyrc.Model.post;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.DonateHolder;

import java.util.ArrayList;

/**
 * Created by root on 11/2/18.
 */

public class DonationAdapter extends BaseAdapter {

    Context c;
    ArrayList <Request> requests;
    LayoutInflater inflater;

    public DonationAdapter(Context c, ArrayList <Request> requests) {
        this.c = c;
        this.requests = requests;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int i) {
        return requests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (inflater == null)
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.donaterequest_row, viewGroup, false);

        DonateHolder donateHolder = new DonateHolder(view);
        String message = " " + requests.get(i).getName() + " " + "Requires " +
                requests.get(i).getRequiredunits() + " Unit of " + requests.get(i).getRequiredbloodgroup() +
                " at" + requests.get(i).getLocation() + " ";
        donateHolder.tvdescription.setText(message);

        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
