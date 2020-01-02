package com.rujira.firebasestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    // Any number you want
    private static final int PICK_IMAGE_CODE = 1000;
    Button btnUpload;
    ImageView imageView;
    AlertDialog dialog;

    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstances();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE) {

            // Show Dialog
            dialog.show();

            UploadTask uploadTask = storageReference.putFile(data.getData());

            // Create task
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String url = task.getResult().toString().substring(0, task.getResult().toString().indexOf("&token"));
                        Log.d("DIRECTLINK", url);
                        Picasso.get().load(url).into(imageView);

                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void initInstances() {
        dialog = new SpotsDialog.Builder().setContext(this).build();
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.imageView);

        // Create name for file
        storageReference = FirebaseStorage.getInstance().getReference("image_upload");
        btnUpload.setOnClickListener(btnUploadListener);
    }

    // Listener Zone
    View.OnClickListener btnUploadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // We will use INTENT to pick image
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_CODE);


        }
    };

}
