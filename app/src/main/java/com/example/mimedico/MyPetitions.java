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

import com.example.mimedico.adapters.MyPetitionsAdapter;
import com.example.mimedico.model.SymptomsPetition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Iterator;

public class MyPetitions extends AppCompatActivity {

    private EditText keyWordsField;
    private FloatingActionButton addPetitionButton;
    private Button searchButton;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_petitions);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        keyWordsField = findViewById(R.id.keyWordsField);
        addPetitionButton = findViewById(R.id.addPetitionButton);
        searchButton = findViewById(R.id.searchButton);

        String keyWords = getIntent().getStringExtra("keyWords");
        if(keyWords == null) {
            getAllItems();
        }else{
            getItemsBySearch(keyWords);
        }


        addPetitionButton.setOnClickListener(this::openSendPetition);
        searchButton.setOnClickListener(this::searchButtonAction);
    }

    public void searchButtonAction(View view){
        Intent intent = new Intent(this, this.getClass());
        intent.putExtra("keyWords",keyWordsField.getText().toString().trim());
        startActivity(intent);
    }

    public void getAllItems(){
        firebaseDatabase.getReference("petitions")
                .orderByChild("user/email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SymptomsPetition> symptomsPetitions = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        while(iterator.hasNext()){
                            SymptomsPetition symptomsPetition = iterator.next().getValue(SymptomsPetition.class);
                            symptomsPetitions.add(symptomsPetition);
                        }
                        MyPetitionsAdapter myPetitionsAdapter = new MyPetitionsAdapter(symptomsPetitions, MyPetitions.this);
                        RecyclerView recyclerView = findViewById(R.id.myPetitionsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyPetitions.this));
                        recyclerView.setAdapter(myPetitionsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getItemsBySearch(String keywords){
        firebaseDatabase.getReference("petitions")
                .orderByChild("user/email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
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
                        MyPetitionsAdapter myPetitionsAdapter = new MyPetitionsAdapter(symptomsPetitions, MyPetitions.this);
                        RecyclerView recyclerView = findViewById(R.id.myPetitionsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyPetitions.this));
                        recyclerView.setAdapter(myPetitionsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void openSendPetition(View view){
        startActivity(new Intent(this, SendPetitions.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}