package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SignupMedic extends AppCompatActivity {

    private int SELECT_PHOTO = 1;

    private Button selectButton;
    private Button sendButton;
    private Uri uri;
    private ImageView imageView;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_medic);

        sendButton = findViewById(R.id.sendButton);
        selectButton = findViewById(R.id.selectImageButton);
        imageView = findViewById(R.id.imageView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void selectImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                sendButton.setVisibility(View.VISIBLE);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendImage(View view){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageReference
                    .child("images")
                    .child(firebaseAuth.getCurrentUser().getEmail())
                    .child("medicProof").putBytes(data);
            uploadTask.addOnFailureListener((Exception exception)->{
                Toast.makeText(getApplicationContext(), "Cannot Send Image",Toast.LENGTH_LONG).show();
            }).addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot)->{
                Toast.makeText(getApplicationContext(), "Image sended correctly",Toast.LENGTH_LONG).show();
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}