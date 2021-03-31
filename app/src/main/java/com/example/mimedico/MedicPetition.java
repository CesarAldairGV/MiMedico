package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mimedico.model.Roles;
import com.example.mimedico.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MedicPetition extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private TextView name, username, email, age;
    private Button accept, reject;
    private ProgressBar photoBar, proofBar;
    private ImageView photoView, proofView;

    private String userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_petition);

        name = findViewById(R.id.medicPetitionTitle);
        username = findViewById(R.id.medicPetitionUsername);
        email = findViewById(R.id.medicPetitionEmail);
        age = findViewById(R.id.medicPetitionDate);

        accept = findViewById(R.id.medicPetitionAccept);
        reject = findViewById(R.id.medicPetitionReject);

        photoBar = findViewById(R.id.photoProgressBar);
        proofBar = findViewById(R.id.proofProgressBar);

        photoView = findViewById(R.id.photoView);
        proofView = findViewById(R.id.proofView);

        firebaseDatabase = FirebaseDatabase.getInstance();

        userId = getIntent().getStringExtra("userId");
        firebaseDatabase.getReference("users")
                .orderByChild("id")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getChildren().iterator().next().getValue(User.class);
                        name.setText(user.getFirstName() + " " + user.getLastName());
                        username.setText(user.getUserName());
                        email.setText(user.getEmail());
                        age.setText(user.getBirthDate());

                        Picasso.get().load(user.getUserPhotoUrl()).into(photoView, new Callback() {
                            @Override
                            public void onSuccess() {
                                photoBar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(Exception e) {
                            }
                        });
                        Picasso.get().load(user.getMedicProofUrl()).into(proofView, new Callback() {
                            @Override
                            public void onSuccess() {
                                proofBar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        reject.setOnClickListener(this::reject);
        accept.setOnClickListener(this::accept);
    }

    public void reject(View view){
        user.setMedicSignUpPetition(false);
        firebaseDatabase.getReference("users")
                .child(userId)
                .setValue(user)
                .addOnSuccessListener(command -> {
                    Toast.makeText(getApplicationContext(), "Rejected Correctly!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, CheckMedicProofsPetitions.class));
                });
    }

    public void accept(View view){
        user.setMedicSignUpPetition(false);
        user.setRole(Roles.MEDIC.toString());
        firebaseDatabase.getReference("users")
                .child(userId)
                .setValue(user)
                .addOnSuccessListener(command -> {
                    Toast.makeText(getApplicationContext(), "Accepted Correctly!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, CheckMedicProofsPetitions.class));
                });
    }
}