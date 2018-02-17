package com.atribus.bloodbankyrc.AdminPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.atribus.bloodbankyrc.Model.post;
import com.atribus.bloodbankyrc.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AdminAddPost extends AppCompatActivity  {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText et_desc, et_title, et_con;
    // DatabaseReference db;
    //   private static final String DB_URL = "https://bloodbank-3c1dd.firebaseio.com/PostDetails";


    FirebaseDatabase database;
    DatabaseReference PostsNode;
    Button btn_submit;
    ImageButton btn_choosepic;
    Uri uri;
    String title, content, description;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_post);
        getSupportActionBar().setTitle("Add new Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        database = FirebaseDatabase.getInstance();

        PostsNode = database.getReferenceFromUrl("https://bloodbank-3c1dd.firebaseio.com/PostDetails");


        et_con = findViewById(R.id.postcontent);
        et_desc = findViewById(R.id.postdescription);
        et_title = findViewById(R.id.posttitle);
        btn_choosepic = findViewById(R.id.choosepic);
        btn_submit = findViewById(R.id.submit);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        btn_choosepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        PICK_IMAGE_REQUEST);

            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startposting();
            }
        });
    }

    private void startposting() {
        final ProgressDialog pd = new ProgressDialog(AdminAddPost.this);
        title = et_title.getText().toString().trim();
        content = et_con.getText().toString().trim();
        description = et_desc.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter All the fields. Dont forget to choose Image.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (uri == null) {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.setMessage("Uploading");
        pd.show();

        final String id = PostsNode.push().getKey();
        mStorageRef.child("post_images").child(id).putFile(uri).addOnSuccessListener(new OnSuccessListener <UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downoaduri = taskSnapshot.getDownloadUrl();
                PostsNode.child(id).setValue(new post(title, content, description, downoaduri.toString()));
                Toast.makeText(getApplicationContext(), "Successfully uploaded.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                finish();

            }
        });
        // startActivity(new Intent(AdminAddPost.this, AdminMain.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            //  Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = findViewById(R.id.choosepic);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        //  startActivity(new Intent(this, AdminMain.class));
    }


}
