package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mimedico.model.User;
import com.example.mimedico.services.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static Intent intentService;

    private Button myMedics;
    private Button mainMyPetitions;
    private Button signupAsMedic;
    private TextView usernameText;
    private TextView emailText;
    private TextView nameText;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainMyPetitions = findViewById(R.id.mainMyPetitionsButton);
        signupAsMedic = findViewById(R.id.mainMedicFormButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usernameText = findViewById(R.id.mainUser);
        emailText = findViewById(R.id.mainEmail);
        nameText = findViewById(R.id.mainName);
        myMedics = findViewById(R.id.mainMyMedicsButton);

        firebaseDatabase.getReference("users")
                .orderByChild("email")
                .equalTo(firebaseAuth.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    user = iterator.next().getValue(User.class);
                    nameText.append(" " + user.getFirstName() + " " + user.getLastName());
                    emailText.append(" " + user.getEmail());
                    usernameText.append(" " + user.getUserName());
                }
                setUpNotificationService();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        mainMyPetitions.setOnClickListener(this::openMyPetitions);
        signupAsMedic.setOnClickListener(this::openSignupAsMedic);
        myMedics.setOnClickListener(this::openMyMedics);
    }


    public void openMyMedics(View view){
        startActivity(new Intent(this, MyMedics.class));
    }

    public void setUpNotificationService(){
        if(intentService == null) {
            intentService = new Intent(this, NotificationService.class);
            intentService.putExtra("userId", user.getId());
            startService(intentService);
        }
    }

    public void openMyPetitions(View view){
        startActivity(new Intent(this, MyPetitions.class));
    }

    public void openSignupAsMedic(View view){
        startActivity(new Intent(this, SignupMedic.class));
    }

    public void logout(){
        stopService(intentService);
        intentService = null;
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