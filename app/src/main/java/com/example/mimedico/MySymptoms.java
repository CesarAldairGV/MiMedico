package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

public class MySymptoms extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_symptoms);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase.getReference("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String userId = snapshot.getChildren().iterator().next().child("id").getValue().toString();
                        firebaseDatabase.getReference("petitions")
                                .orderByChild("userId")
                                .equalTo(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Iterator<DataSnapshot> dataSnapshotIterator = snapshot.getChildren().iterator();
                                        List<SymptomsPetition> symptomsPetitions = new ArrayList<>();
                                        while(dataSnapshotIterator.hasNext()){
                                            symptomsPetitions.add(dataSnapshotIterator.next().getValue(SymptomsPetition.class));
                                        }
                                        //symptomsPetitions.add(null);
                                        MyPetitionsAdapter myPetitionsAdapter = new MyPetitionsAdapter(symptomsPetitions, MySymptoms.this);
                                        RecyclerView recyclerView = findViewById(R.id.mySymptomsList);
                                        //recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(MySymptoms.this));
                                        recyclerView.setAdapter(myPetitionsAdapter);
                                        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                            @Override
                                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                                super.onScrolled(recyclerView, dx, dy);
                                            }
                                        });*/
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

    public void openSendPetition(View view){
        startActivity(new Intent(this, SendSymptoms.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}