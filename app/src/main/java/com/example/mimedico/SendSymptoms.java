package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.view.View;
import android.widget.Toast;

import com.example.mimedico.model.SymptomsPetition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.UUID;

public class SendSymptoms extends AppCompatActivity {

    private EditText symptomsField;
    private EditText titleField;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_symptoms);

        symptomsField = findViewById(R.id.sendSymptomsField);
        titleField = findViewById(R.id.sendTitleField);
        progressBar = findViewById(R.id.sendSymptomsProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
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
                        userId = snapshot.getChildren().iterator().next().child("id").getValue().toString();
                        SymptomsPetition symptomsPetition = SymptomsPetition.builder()
                                .id(UUID.randomUUID().toString())
                                .userId(userId)
                                .description(symptoms)
                                .petitionDate(new Date().toString())
                                .petitionAccepted(false)
                                .title(title)
                                .build();
                        firebaseDatabase.getReference()
                                .child("petitions")
                                .child(UUID.randomUUID().toString())
                                .setValue(symptomsPetition)
                                .addOnSuccessListener(command ->{
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