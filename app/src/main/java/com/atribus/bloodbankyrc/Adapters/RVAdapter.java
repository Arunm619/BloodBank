package com.atribus.bloodbankyrc.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atribus.bloodbankyrc.Model.User;
import com.atribus.bloodbankyrc.Model.UserDistanceDetails;
import com.atribus.bloodbankyrc.R;

import java.util.List;

/**
 * Created by root on 10/2/18.
 */

public class RVAdapter extends RecyclerView.Adapter <RVAdapter.MyViewHolder> {
    private List <UserDistanceDetails> usersList;

    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public  void setOnItemClickListener (OnItemClickListener listener)
    {
        onItemClickListener = listener;

    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvbloodgroup;
        TextView tvdistanceaway;

        CardView cv_user_single;

        MyViewHolder(View view, final OnItemClickListener onItemClickListener) {
            super(view);
            tvName = view.findViewById(R.id.tvname);
            tvbloodgroup = view.findViewById(R.id.tvbloodgroup);
            tvdistanceaway = view.findViewById(R.id.tvdistanceaway);
            cv_user_single = view.findViewById(R.id.cv_user_single);
   view.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           if (onItemClickListener!=null)
           {
               int pos =getAdapterPosition();
               if (pos!=RecyclerView.NO_POSITION)
               {
                   onItemClickListener.onItemClick(pos);
               }
           }
       }
   });
        }
    }


    public RVAdapter(List <UserDistanceDetails> usersList) {
        this.usersList = usersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_row_donor, parent, false);


        return new MyViewHolder(itemView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final UserDistanceDetails details = usersList.get(position);

        User user = details.getUser();
        if (user != null) {
            holder.tvName.setText(user.getName());
            holder.tvbloodgroup.setText(user.getBloodgroup());
            holder.tvdistanceaway.setText(String.valueOf((int) details.getDistance()) + " KMs away");
            }
        //PicassoClient.downloadimg(this,user.);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
