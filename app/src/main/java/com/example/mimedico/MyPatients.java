package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mimedico.adapters.MyMedicsAdapter;
import com.example.mimedico.adapters.MyPatientsAdapter;
import com.example.mimedico.model.Consult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyPatients extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseDatabase.getReference("consults")
                .orderByChild("medic/email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        List<Consult> consults = new ArrayList<>();
                        while(iterator.hasNext()){
                            Consult consult = iterator.next().getValue(Consult.class);
                            consults.add(consult);
                        }
                        MyPatientsAdapter myPatientsAdapter = new MyPatientsAdapter(consults, MyPatients.this);
                        RecyclerView recyclerView = findViewById(R.id.myPatientsList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPatients.this);
                        linearLayoutManager.setReverseLayout(true);
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(myPatientsAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}