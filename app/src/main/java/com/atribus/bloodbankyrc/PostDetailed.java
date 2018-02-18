package com.atribus.bloodbankyrc;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.atribus.bloodbankyrc.Model.post;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class PostDetailed extends AppCompatActivity {

    TextView tvmtitle, tvmcon, tvmdesc;
    ImageView iv;
    FloatingActionButton btn_share;
    String pt, pc, pd, pi;
    post pfb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detailed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        Gson gson = new Gson();
        String data;
        data = getIntent().getExtras().getString("obj", null);
        post post;
        tvmcon = findViewById(R.id.mcon);
        tvmdesc = findViewById(R.id.mdesc);
        tvmtitle = findViewById(R.id.mtitle);
        iv = findViewById(R.id.imgview);
        //  btn_share = findViewById(R.id.share);


        if (data != null) {
            post = gson.fromJson(data, post.class);

            pfb = post;

            pt = post.getPost_title();
            tvmtitle.setText(pt);
            pd = post.getPost_description();
            tvmdesc.setText(pd);
            pc = post.getPost_content();
            tvmcon.setText(pc);
            pi = post.getImg_path();

            drawimg(pi);
        }
       /* btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  sharelink();
            }
        });*/

    }

    private void drawimg(String url) {
        if (url != null && url.length() > 0) {
            Picasso.with(this).load(url).placeholder(R.drawable.placeholder).into(iv);

        } else {
            Picasso.with(this).load(R.drawable.placeholder).into(iv);
        }

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
