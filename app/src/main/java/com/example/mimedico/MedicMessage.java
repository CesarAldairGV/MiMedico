package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mimedico.model.Consult;
import com.example.mimedico.model.Message;
import com.example.mimedico.model.Notification;
import com.example.mimedico.model.NotificationType;
import com.example.mimedico.model.SymptomsPetition;
import com.example.mimedico.model.SymptomsPetitionMessage;
import com.example.mimedico.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.UUID;

public class MedicMessage extends AppCompatActivity {

    private TextView name, institution, years, email, message;
    private Button accept, reject;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private ImageView photoView, proofView;


    private String petitionId;
    private String messageId;
    private User medic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_message);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        photoView = findViewById(R.id.medicMessagePhotoViewReal);
        proofView = findViewById(R.id.medicMessageProofView);

        name = findViewById(R.id.medicMessageName);
        institution = findViewById(R.id.medicMessageInstitution);
        email = findViewById(R.id.medicMessageEmail);
        years = findViewById(R.id.medicMessageYears);
        message = findViewById(R.id.medicMessageMessage);

        accept = findViewById(R.id.medicMessageAccept);
        //reject = findViewById(R.id.medicMessageReject);

        petitionId = getIntent().getStringExtra("petitionId");
        messageId = getIntent().getStringExtra("messageId");

        progressBar = findViewById(R.id.medicMessageProgressBar);

        firebaseDatabase.getReference("petitions")
                .child(petitionId)
                .child("messages")
                .child(messageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        SymptomsPetitionMessage symptomsPetitionMessage = snapshot.getValue(SymptomsPetitionMessage.class);
                        medic = symptomsPetitionMessage.getMedic();
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

        accept.setOnClickListener(this::accept);
    }

    public void accept(View view){
        progressBar.setVisibility(View.VISIBLE);
        firebaseDatabase.getReference("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getChildren().iterator().next().getValue(User.class);
                        firebaseDatabase.getReference("petitions")
                                .orderByChild("id")
                                .equalTo(petitionId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        SymptomsPetition symptomsPetition = snapshot.getChildren().iterator().next().getValue(SymptomsPetition.class);
                                        symptomsPetition.setPetitionAccepted(true);
                                        symptomsPetition.setMedic(medic);
                                        firebaseDatabase.getReference("petitions")
                                                .child(symptomsPetition.getId())
                                                .setValue(symptomsPetition)
                                                .addOnSuccessListener(command -> {
                                                    Consult consult = Consult.builder()
                                                            .id(firebaseDatabase.getReference("consults").push().getKey())
                                                            .date(new Date().toString())
                                                            .medic(medic)
                                                            .symptomsPetition(symptomsPetition)
                                                            .user(user)
                                                            .build();
                                                    firebaseDatabase.getReference("consults")
                                                            .child(consult.getId())
                                                            .setValue(consult)
                                                            .addOnSuccessListener(command2 -> {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(getApplicationContext(), "Medic Accepted", Toast.LENGTH_LONG).show();
                                                                Notification notification = Notification.builder()
                                                                        .id(firebaseDatabase.getReference("users")
                                                                                .child(symptomsPetition.getUser().getId())
                                                                                .child("notifications")
                                                                                .push().getKey())
                                                                        .title("A user accepted your consult")
                                                                        .message(symptomsPetition.getUser().getFirstName() + " "
                                                                                + symptomsPetition.getUser().getLastName() +
                                                                                "accepted your consult for the petition: "
                                                                                +symptomsPetition.getTitle())
                                                                        .otherId(consult.getId())
                                                                        .type(NotificationType.USER_ACCEPT.getType())
                                                                        .build();
                                                                firebaseDatabase.getReference("users")
                                                                        .child(medic.getId())
                                                                        .child("notifications")
                                                                        .child(notification.getId())
                                                                        .setValue(notification);
                                                                Intent intent = new Intent(MedicMessage.this, ChatUser.class);
                                                                intent.putExtra("consultId",consult.getId());
                                                                MedicMessage.this.startActivity(intent);
                                                            });
                                                });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}