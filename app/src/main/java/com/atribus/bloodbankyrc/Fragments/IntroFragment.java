package com.atribus.bloodbankyrc.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atribus.bloodbankyrc.R;

/**
 * Created by root on 15/2/18.
 */

public class IntroFragment extends Fragment {
    private TextView textViewfordesc, tvfortitle;
    private ImageView imgview;
    private String title;

    private String desc;
    private int drawable;

    public IntroFragment() {
        // Required empty public constructor
    }


    public void setimg(final Integer drawable) {
        this.drawable = drawable;

        if (imgview != null) {
            imgview.setImageResource(drawable);
        }
    }

    public void settitle(final String title) {
        this.title = title;

        if (tvfortitle != null) {
            tvfortitle.setText(title);
        }
    }


    public void setdesc(final String desc) {
        this.desc = desc;

        if (textViewfordesc != null) {
            textViewfordesc.setText(desc);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_intro, container, false);

        textViewfordesc = root.findViewById(R.id.titleda);
        tvfortitle = root.findViewById(R.id.desc);
        imgview = root.findViewById(R.id.imgview);
        tvfortitle.setText(title);
        textViewfordesc.setText(desc);
        imgview.setImageResource(drawable);

        return root;
    }

}