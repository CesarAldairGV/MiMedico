package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mimedico.adapters.MedicPetitionsAdapter;
import com.example.mimedico.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CheckMedicPetitions extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_medic_petitions);

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseDatabase.getReference("users")
                .orderByChild("medicSignUpPetition")
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        List<User> medics = new ArrayList<>();
                        while(iterator.hasNext()){
                            User medic = iterator.next().getValue(User.class);
                            medics.add(medic);
                        }
                        MedicPetitionsAdapter medicPetitionsAdapter = new MedicPetitionsAdapter(medics, CheckMedicPetitions.this);
                        RecyclerView recyclerView = findViewById(R.id.checkMedicPetitionsList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CheckMedicPetitions.this);
                        linearLayoutManager.setReverseLayout(true);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(medicPetitionsAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainAdmin.class));
    }
}