package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mimedico.adapters.CheckPetitionsAdapter;
import com.example.mimedico.adapters.MyPetitionsAdapter;
import com.example.mimedico.model.SymptomsPetition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CheckPetitions extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_petitions);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase.getReference("petitions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> dataSnapshotIterator = snapshot.getChildren().iterator();
                        List<SymptomsPetition> symptomsPetitions = new ArrayList<>();
                        while(dataSnapshotIterator.hasNext()){
                            symptomsPetitions.add(dataSnapshotIterator.next().getValue(SymptomsPetition.class));
                        }
                        CheckPetitionsAdapter checkPetitionsAdapter = new CheckPetitionsAdapter(symptomsPetitions, CheckPetitions.this);
                        RecyclerView recyclerView = findViewById(R.id.checkPetitionsList);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CheckPetitions.this));
                        recyclerView.setAdapter(checkPetitionsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}