package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mimedico.model.SymptomsPetition;
import com.example.mimedico.model.SymptomsPetitionMessage;
import com.example.mimedico.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.UUID;

public class CheckOnePetition extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private TextView titleText, usernameText, firstnameText, lastnameText, emailText, dateText, descriptionText;
    private ImageView imageView;
    private Button sendButton;
    private EditText messageField;
    private ProgressBar progressBar;

    private SymptomsPetition symptomsPetition;
    private String symptomsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_one_petition);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        usernameText = findViewById(R.id.checkOnePetitionUsername);
        firstnameText = findViewById(R.id.checkOnePetitionFirstname);
        lastnameText = findViewById(R.id.checkOnePetitionLastname);
        emailText = findViewById(R.id.checkOnePetitionEmail);
        dateText = findViewById(R.id.checkOnePetitionDate);
        titleText = findViewById(R.id.checkOnePetitionTitle);
        descriptionText = findViewById(R.id.checkOnePetitionDescription);
        sendButton = findViewById(R.id.checkOnePetitionSend);
        messageField = findViewById(R.id.checkOnePetitionMessage);
        imageView = findViewById(R.id.checkOnePetitionImage);
        progressBar = findViewById(R.id.checkOnePetitionProgressBar);

        symptomsId = getIntent().getStringExtra("petitionId");

        populateText();

        sendButton.setOnClickListener(this::sendMessage);
    }

    public void sendMessage(View view){
        String message = messageField.getText().toString().trim();
        if(message.length() < 1){
            messageField.setError("Message must be not empty");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        firebaseDatabase.getReference("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User medic = snapshot.getChildren().iterator().next().getValue(User.class);
                        SymptomsPetitionMessage symptomsPetitionsResponse = SymptomsPetitionMessage.builder()
                                .id(UUID.randomUUID().toString())
                                .medic(medic)
                                .date(new Date().toString())
                                .message(message)
                                .build();
                        firebaseDatabase.getReference("petitions")
                                .child(symptomsId)
                                .child("messages")
                                .child(symptomsPetitionsResponse.getId())
                                .setValue(symptomsPetitionsResponse)
                                .addOnSuccessListener(command ->
                                        Toast.makeText(getApplicationContext(), "Message Sent correcly!",Toast.LENGTH_LONG).show())
                                .addOnFailureListener(command ->
                                        Toast.makeText(getApplicationContext(), "Cannot send message!",Toast.LENGTH_LONG).show())
                                .addOnCompleteListener(command ->
                                        progressBar.setVisibility(View.GONE));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void populateText(){
        firebaseDatabase.getReference("petitions")
                .orderByKey()
                .equalTo(symptomsId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        symptomsPetition = snapshot.getChildren().iterator().next().getValue(SymptomsPetition.class);
                        usernameText.setText(symptomsPetition.getUser().getUserName());
                        firstnameText.setText(symptomsPetition.getUser().getFirstName());
                        lastnameText.setText(symptomsPetition.getUser().getLastName());
                        emailText.setText(symptomsPetition.getUser().getEmail());
                        dateText.setText(symptomsPetition.getPetitionDate());
                        titleText.setText(symptomsPetition.getTitle());
                        descriptionText.setText(symptomsPetition.getDescription());
                        if(symptomsPetition.isImage()){
                            imageView.setVisibility(View.VISIBLE);
                            Picasso.get().load(symptomsPetition.getImageUri()).into(imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}