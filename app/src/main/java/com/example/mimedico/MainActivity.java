package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.text.Edits;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mimedico.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.Locale;

import static com.example.mimedico.utils.ChangeLanguage.changeLanguage;

public class MainActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView emailText;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usernameText = findViewById(R.id.mainUser);
        emailText = findViewById(R.id.mainEmail);

        firebaseDatabase.getReference("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot dataSnapshot1 = iterator.next();
                    String email = dataSnapshot1.child("email").getValue().toString();
                    String username = dataSnapshot1.child("username").getValue().toString();
                    emailText.append(" " + email);
                    usernameText.append(" " + username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void openMySymptoms(View view){
        startActivity(new Intent(this, MySymptoms.class));
    }

    public void openSignupAsMedic(View view){
        startActivity(new Intent(this, SignupMedic.class));
    }

    public void logout(){
        firebaseAuth.signOut();
        startActivity(new Intent(this, Login.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                logout();
                return true;
            case R.id.mainLanguages:
                Intent intent = new Intent(this, Language.class);
                intent.putExtra("parent","MainActivity");
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
    }
}