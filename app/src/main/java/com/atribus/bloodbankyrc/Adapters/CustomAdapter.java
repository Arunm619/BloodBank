package com.atribus.bloodbankyrc.Adapters;

/**
 * Created by root on 2/12/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atribus.bloodbankyrc.Model.post;
import com.atribus.bloodbankyrc.PicassoClient;
import com.atribus.bloodbankyrc.R;
import com.atribus.bloodbankyrc.Utils.MyHolder;

import java.util.ArrayList;

/**
 * Created by Admin on 5/26/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList <post> posts;
    LayoutInflater inflater;


    public CustomAdapter(Context c, ArrayList <post> posts) {
        this.c = c;
        this.posts = posts;
    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {

        if (inflater == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.postrow, viewGroup, false);

        }

        MyHolder holder = new MyHolder(convertview);
        if (!posts.isEmpty()) {
            holder.tvtitle.setText(posts.get(i).getPost_title());
            holder.tvdescription.setText(posts.get(i).getPost_description());


            holder.timestamp.setText(posts.get(i).getDatecreated());
            PicassoClient.downloadimg(c, posts.get(i).getImg_path(), holder.img);

        }

        return convertview;
    }

}