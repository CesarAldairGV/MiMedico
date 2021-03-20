package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.view.View;
import android.widget.Toast;

import com.example.mimedico.model.SymptomsPetition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class SendSymptoms extends AppCompatActivity {

    private EditText symptomsField;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_symptoms);

        symptomsField = findViewById(R.id.sendSymptomsField);
        progressBar = findViewById(R.id.sendSymptomsProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void sendSymptoms(View view){
        String symptoms = symptomsField.getText().toString();
        if(symptoms.trim().length() == 0){
            symptomsField.setError("You must write something!");
            return;
        }
        SymptomsPetition symptomsPetition = SymptomsPetition.builder()
                .userEmail(firebaseAuth.getCurrentUser().getEmail())
                .description(symptoms)
                .petitionDate(new Date())
                .petitionAccepted(false)
                .build();
        firebaseDatabase.getReference().child("petitions").push().setValue(symptomsPetition)
                .addOnSuccessListener(command ->{
                    Toast.makeText(getApplicationContext(), "Petition Sent Correctly!",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MySymptoms.class));
                })
                .addOnFailureListener(command -> {
                    Toast.makeText(getApplicationContext(), "Petition Not Sent, Something Went Wrong!",Toast.LENGTH_LONG);
                });
    }
}