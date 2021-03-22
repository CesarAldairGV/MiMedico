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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.view.View;
import android.widget.Toast;

import com.example.mimedico.model.SymptomsPetition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class SendSymptoms extends AppCompatActivity {

    private int SELECT_PHOTO = 1;

    private EditText symptomsField;
    private EditText titleField;
    private Uri uri;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button appendImageButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private boolean imageSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_symptoms);

        symptomsField = findViewById(R.id.sendSymptomsField);
        titleField = findViewById(R.id.sendTitleField);
        progressBar = findViewById(R.id.sendSymptomsProgressBar);
        imageView = findViewById(R.id.imageView2);
        appendImageButton = findViewById(R.id.appendImageButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        appendImageButton.setOnClickListener(this::appendImage);
    }

    public void appendImage(View view){
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
                imageSelected = true;
            }catch(FileNotFoundException e){
                e.printStackTrace();
                imageSelected = false;
                uri = null;
            }catch(IOException e){
                e.printStackTrace();
                imageSelected = false;
                uri = null;
            }
        }
    }

    public void sendSymptoms(View view){
        String title = titleField.getText().toString();
        String symptoms = symptomsField.getText().toString();
        if(symptoms.trim().length() == 0){
            symptomsField.setError("You must write something!");
            return;
        }
        if(title.trim().length() == 0){
            symptomsField.setError("You must write something!");
            return;
        }

        firebaseDatabase.getReference()
                .child("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String userId = snapshot.getChildren().iterator().next().child("id").getValue().toString();
                        SymptomsPetition symptomsPetition = SymptomsPetition.builder()
                                .id(UUID.randomUUID().toString())
                                .userId(userId)
                                .description(symptoms)
                                .petitionDate(new Date().toString())
                                .petitionAccepted(false)
                                .title(title)
                                .hasImage(imageSelected)
                                .build();
                        firebaseDatabase.getReference()
                                .child("petitions")
                                .child(symptomsPetition.getId())
                                .setValue(symptomsPetition)
                                .addOnSuccessListener(command ->{
                                    if(imageSelected){
                                        try{
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();
                                            firebaseStorage.getReference("petitions")
                                                    .child(symptomsPetition.getId())
                                                    .putBytes(data);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), "Petition Sent Correctly!",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(SendSymptoms.this, MySymptoms.class));
                                })
                                .addOnFailureListener(command -> {
                                    Toast.makeText(getApplicationContext(), "Petition Not Sent, Something Went Wrong!",Toast.LENGTH_LONG);
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}