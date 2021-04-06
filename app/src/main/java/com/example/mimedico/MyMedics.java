package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mimedico.adapters.MyMedicsAdapter;
import com.example.mimedico.adapters.MyPetitionsAdapter;
import com.example.mimedico.model.Consult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyMedics extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medics);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseDatabase.getReference("consults")
                .orderByChild("user/email")
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
                        MyMedicsAdapter myMedicsAdapter = new MyMedicsAdapter(consults, MyMedics.this);
                        RecyclerView recyclerView = findViewById(R.id.myMedicsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyMedics.this));
                        recyclerView.setAdapter(myMedicsAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}