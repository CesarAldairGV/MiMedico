package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mimedico.adapters.CheckPetitionsAdapter;
import com.example.mimedico.model.SymptomsPetition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Iterator;

public class CheckPetitions extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

    private Button searchButton;
    private EditText keyWordsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_petitions);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        searchButton = findViewById(R.id.checkMedicPetitionsButton);
        keyWordsField = findViewById(R.id.checkMedicPetitionsField);

        String keyWords = getIntent().getStringExtra("keyWords");
        if(keyWords == null) {
            getAllItems();
        }else{
            getItemsBySearch(keyWords);
        }

        searchButton.setOnClickListener(this::searchButtonAction);
    }

    public void getAllItems(){
        firebaseDatabase.getReference("petitions")
                .orderByChild("petitionAccepted")
                .equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SymptomsPetition> symptomsPetitions = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        while(iterator.hasNext()){
                            SymptomsPetition symptomsPetition = iterator.next().getValue(SymptomsPetition.class);
                            symptomsPetitions.add(symptomsPetition);
                        }
                        CheckPetitionsAdapter myPetitionsAdapter = new CheckPetitionsAdapter(symptomsPetitions, CheckPetitions.this);
                        RecyclerView recyclerView = findViewById(R.id.checkMedicPetitionsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CheckPetitions.this));
                        recyclerView.setAdapter(myPetitionsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getItemsBySearch(String keywords){
        firebaseDatabase.getReference("petitions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SymptomsPetition> symptomsPetitions = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        while(iterator.hasNext()){
                            SymptomsPetition symptomsPetition = iterator.next().getValue(SymptomsPetition.class);
                            if(symptomsPetition.getTitle().toLowerCase().contains(keywords.toLowerCase()) ||
                                    symptomsPetition.getDescription().toLowerCase().contains(keywords.toLowerCase()))
                                symptomsPetitions.add(symptomsPetition);
                        }
                        CheckPetitionsAdapter myPetitionsAdapter = new CheckPetitionsAdapter(symptomsPetitions, CheckPetitions.this);
                        RecyclerView recyclerView = findViewById(R.id.checkMedicPetitionsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CheckPetitions.this));
                        recyclerView.setAdapter(myPetitionsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void searchButtonAction(View view){
        Intent intent = new Intent(this, this.getClass());
        intent.putExtra("keyWords",keyWordsField.getText().toString().trim());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainMedic.class));
    }
}