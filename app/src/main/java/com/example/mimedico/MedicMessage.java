package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mimedico.model.SymptomsPetitionMessage;
import com.example.mimedico.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MedicMessage extends AppCompatActivity {

    private TextView name, institution, years, email, message;
    private Button accept, reject;
    private FirebaseDatabase firebaseDatabase;

    private ImageView photoView, proofView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_message);

        firebaseDatabase = FirebaseDatabase.getInstance();
        photoView = findViewById(R.id.medicMessagePhotoView);
        proofView = findViewById(R.id.medicMessageProofView);

        name = findViewById(R.id.medicMessageName);
        institution = findViewById(R.id.medicMessageInstitution);
        email = findViewById(R.id.medicMessageEmail);
        years = findViewById(R.id.medicMessageYears);
        message = findViewById(R.id.medicMessageMessage);

        accept = findViewById(R.id.medicMessageAccept);
        reject = findViewById(R.id.medicMessageReject);

        String petitionId = getIntent().getStringExtra("petitionId");
        String messageId = getIntent().getStringExtra("messageId");

        firebaseDatabase.getReference("petitions")
                .child(petitionId)
                .child("messages")
                .child(messageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        SymptomsPetitionMessage symptomsPetitionMessage = snapshot.getValue(SymptomsPetitionMessage.class);
                        User medic = symptomsPetitionMessage.getMedic();
                        name.append(" " + medic.getFirstName() + " " + medic.getLastName());
                        institution.append(" " + medic.getInstitution());
                        email.append(" " + medic.getEmail());
                        years.append(" " + medic.getYearsOfExperience());
                        message.setText(symptomsPetitionMessage.getMessage());
                        Picasso.get().load(medic.getMedicProofUrl()).fit().centerCrop().into(proofView);
                        Picasso.get().load(medic.getUserPhotoUrl()).fit().centerCrop().into(photoView);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}