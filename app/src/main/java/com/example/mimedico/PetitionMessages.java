package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mimedico.adapters.CheckPetitionsAdapter;
import com.example.mimedico.adapters.PetitionMessagesAdapter;
import com.example.mimedico.model.SymptomsPetition;
import com.example.mimedico.model.SymptomsPetitionMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PetitionMessages extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    public String petitionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petition_messages);

        petitionId = getIntent().getStringExtra("petitionId");

        firebaseDatabase = FirebaseDatabase.getInstance();

        fillMessagesList();
    }

    public void fillMessagesList(){
        firebaseDatabase.getReference("petitions")
                .child(petitionId)
                .child("messages")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        List<SymptomsPetitionMessage> symptomsPetitionMessageList = new ArrayList<>();
                        while(iterator.hasNext()){
                            SymptomsPetitionMessage symptomsPetitionMessage = iterator.next().getValue(SymptomsPetitionMessage.class);
                            symptomsPetitionMessageList.add(symptomsPetitionMessage);
                        }
                        PetitionMessagesAdapter petitionMessagesAdapter = new PetitionMessagesAdapter(symptomsPetitionMessageList, PetitionMessages.this);
                        RecyclerView recyclerView = findViewById(R.id.petitionMessagesList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PetitionMessages.this));
                        recyclerView.setAdapter(petitionMessagesAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}