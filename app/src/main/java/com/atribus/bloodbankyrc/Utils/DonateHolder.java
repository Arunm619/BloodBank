package com.atribus.bloodbankyrc.Utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atribus.bloodbankyrc.R;

/**
 * Created by root on 11/2/18.
 */

public class DonateHolder {
    public TextView tvdescription;

    public ImageView img;
//    public TextView tvtitle;


    public DonateHolder(View itemView) {


        img = itemView.findViewById(R.id.imgview);
        tvdescription = itemView.findViewById(R.id.tvmessage);
        // btn_learn = itemView.findViewById(R.id.btn_learn);

    }
}
