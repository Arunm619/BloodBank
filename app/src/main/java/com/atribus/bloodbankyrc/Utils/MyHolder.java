package com.atribus.bloodbankyrc.Utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atribus.bloodbankyrc.R;


public class MyHolder {
    public TextView tvdescription;

    public ImageView img;
    public TextView tvtitle;
    public TextView timestamp;


    public MyHolder(View itemView) {


        tvtitle = itemView.findViewById(R.id.tvTitle);
        img = itemView.findViewById(R.id.ivpost);
        tvdescription = itemView.findViewById(R.id.tvDescription);
        timestamp=itemView.findViewById(R.id.timestamp);
       // btn_learn = itemView.findViewById(R.id.btn_learn);

    }
}
