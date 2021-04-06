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
import com.example.mimedico.model.User;
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

public class SendPetitions extends AppCompatActivity {

    private int SELECT_PHOTO = 1;

    private EditText symptomsField;
    private EditText titleField;
    private Uri uri;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button appendImageButton;
    private Button sendPetitionButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    private boolean imageSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_petitions);

        symptomsField = findViewById(R.id.sendPetitionSymptomsField);
        titleField = findViewById(R.id.sendPetitionTitleField);
        progressBar = findViewById(R.id.sendPetitionProgressBar);
        imageView = findViewById(R.id.sendPetitionImageView);
        appendImageButton = findViewById(R.id.appendImageButton);
        sendPetitionButton = findViewById(R.id.sendPetitionButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        sendPetitionButton.setOnClickListener(this::sendPetition);
        appendImageButton.setOnClickListener(this::appendImage);
    }

    public void appendImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                imageSelected = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                imageSelected = false;
                uri = null;
            } catch (IOException e) {
                e.printStackTrace();
                imageSelected = false;
                uri = null;
            }
        }
    }

    public void sendPetition(View view) {
        String title = titleField.getText().toString();
        String symptoms = symptomsField.getText().toString();
        if (symptoms.trim().length() == 0) {
            symptomsField.setError("You must write something!");
            return;
        }
        if (title.trim().length() == 0) {
            symptomsField.setError("You must write something!");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseDatabase.getReference()
                .child("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final User user = snapshot.getChildren().iterator().next().getValue(User.class);
                        SymptomsPetition symptomsPetition = SymptomsPetition.builder()
                                .id(firebaseDatabase.getReference("petitions").push().getKey())
                                .user(user)
                                .description(symptoms)
                                .petitionDate(new Date().toString())
                                .petitionAccepted(false)
                                .title(title)
                                .image(imageSelected)
                                .build();
                        if (imageSelected) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();
                                firebaseStorage.getReference("petitions")
                                        .child(symptomsPetition.getId())
                                        .putBytes(data)
                                        .addOnSuccessListener(command -> {
                                            firebaseStorage.getReference("petitions")
                                                    .child(symptomsPetition.getId())
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(uri1 -> {
                                                        symptomsPetition.setImageUri(uri1.toString());
                                                        firebaseDatabase.getReference("petitions")
                                                                .child(symptomsPetition.getId())
                                                                .setValue(symptomsPetition)
                                                                .addOnSuccessListener(command2 -> {
                                                                    Toast.makeText(getApplicationContext(), "Petition Sent Correctly!", Toast.LENGTH_LONG).show();
                                                                    startActivity(new Intent(SendPetitions.this, MyPetitions.class));
                                                                })
                                                                .addOnFailureListener(command3 -> {
                                                                    Toast.makeText(getApplicationContext(), "Petition Not Sent, Something Went Wrong!", Toast.LENGTH_LONG);
                                                                })
                                                        .addOnCompleteListener(command1 -> progressBar.setVisibility(View.GONE));
                                                    });
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            firebaseDatabase.getReference()
                                    .child("petitions")
                                    .child(symptomsPetition.getId())
                                    .setValue(symptomsPetition)
                                    .addOnSuccessListener(command2 -> {
                                        Toast.makeText(getApplicationContext(), "Petition Sent Correctly!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SendPetitions.this, MyPetitions.class));
                                    })
                                    .addOnFailureListener(command3 -> {
                                        Toast.makeText(getApplicationContext(), "Petition Not Sent, Something Went Wrong!", Toast.LENGTH_LONG);
                                    })
                                    .addOnCompleteListener(command1 -> progressBar.setVisibility(View.GONE));;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}