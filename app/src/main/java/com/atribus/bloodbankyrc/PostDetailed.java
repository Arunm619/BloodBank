package com.atribus.bloodbankyrc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atribus.bloodbankyrc.Model.post;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class PostDetailed extends AppCompatActivity {

    TextView tvmtitle, tvmcon, tvmdesc;
    ImageView iv;
    FloatingActionButton btn_share;
    String pt, pc, pd, pi, url;
    post pfb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detailed);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Post");
        }
        Gson gson = new Gson();
        String data;
        data = getIntent().getExtras().getString("obj", null);
        post post;
        tvmcon = findViewById(R.id.mcon);
        tvmdesc = findViewById(R.id.mdesc);
        tvmtitle = findViewById(R.id.mtitle);
        iv = findViewById(R.id.imgview);
        btn_share = findViewById(R.id.btn_share);


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

            url = post.getUrl();
            // Toast.makeText(this, "URL " + post.getUrl(), Toast.LENGTH_SHORT).show();
            drawimg(pi);
        }
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharelink();
            }
        });

    }

    private void sharelink() {

        if (url != null) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
           /* Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
*/
            String message = pt + "Read more at this link:\n" + url + "\n For more contents , Install App at " + getString(R.string.applink);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);


        }
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
