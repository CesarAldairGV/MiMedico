package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainMedic extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private TextView usernameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medic);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usernameText = findViewById(R.id.medicUser);
        emailText = findViewById(R.id.medicEmail);

        firebaseDatabase.getReference("users").orderByChild("email").equalTo(firebaseAuth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot dataSnapshot1 = iterator.next();
                    String email = dataSnapshot1.child("email").getValue().toString();
                    String username = dataSnapshot1.child("username").getValue().toString();
                    emailText.append(email);
                    usernameText.append(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void openSignupAsMedic(View view){
        startActivity(new Intent(this, SignupMedic.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                logout();
                return true;
            case R.id.mainLanguages:
                Intent intent = new Intent(this, Language.class);
                intent.putExtra("parent","MedicActivity");
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void logout(){
        firebaseAuth.signOut();
        startActivity(new Intent(this, Login.class));
    }

    @Override
    public void onBackPressed() {
    }
}