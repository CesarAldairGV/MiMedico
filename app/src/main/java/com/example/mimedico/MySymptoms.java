package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.mimedico.adapters.MyPetitionsAdapter;
import com.example.mimedico.dto.MySymptomsPetitionDto;
import com.example.mimedico.model.SymptomsPetition;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MySymptoms extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private final int TEN_MEGABYTES = 1024 * 1024 * 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_symptoms);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
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
                                        List<MySymptomsPetitionDto> symptomsPetitions = new ArrayList<>();

                                        MyPetitionsAdapter myPetitionsAdapter = new MyPetitionsAdapter(symptomsPetitions, MySymptoms.this);
                                        RecyclerView recyclerView = findViewById(R.id.mySymptomsList);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(MySymptoms.this));
                                        recyclerView.setAdapter(myPetitionsAdapter);

                                        while(dataSnapshotIterator.hasNext()){
                                            MySymptomsPetitionDto symptomsPetition = dataSnapshotIterator.next().getValue(MySymptomsPetitionDto.class);
                                            if(symptomsPetition.isHasImage()){
                                                firebaseStorage.getReference("petitions")
                                                        .child(symptomsPetition.getId())
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(uri -> {
                                                            symptomsPetition.setImage(uri);
                                                            symptomsPetitions.add(symptomsPetition);
                                                            Collections.sort(symptomsPetitions);
                                                            myPetitionsAdapter.notifyItemChanged(symptomsPetitions.size() - 1);
                                                        });
                                            }else {
                                                symptomsPetitions.add(symptomsPetition);
                                                Collections.sort(symptomsPetitions);
                                                myPetitionsAdapter.notifyItemChanged(symptomsPetitions.size() - 1);
                                            }
                                        }
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