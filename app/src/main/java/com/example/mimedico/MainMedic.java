package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mimedico.model.User;
import com.example.mimedico.services.NotificationService;
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
    private TextView nameText;

    private Button checkPetitionsButton;
    private Button myPatientsButton;

    private static Intent intentService;
    private User medic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medic);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        usernameText = findViewById(R.id.medicUser);
        emailText = findViewById(R.id.medicEmail);
        nameText = findViewById(R.id.medicName);

        checkPetitionsButton = findViewById(R.id.medicCheckMedicProofButton);
        myPatientsButton = findViewById(R.id.medicMyPatientsButton);

        firebaseDatabase.getReference("users").orderByChild("email").equalTo(firebaseAuth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    medic = iterator.next().getValue(User.class);
                    nameText.append(" " + medic.getFirstName() + " " + medic.getLastName());
                    emailText.append(" " + medic.getEmail());
                    usernameText.append(" " + medic.getUserName());
                }
                setUpNotificationService();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        myPatientsButton.setOnClickListener(this::openMyPatients);
    }

    public void setUpNotificationService(){
        if(intentService == null) {
            intentService = new Intent(this, NotificationService.class);
            intentService.putExtra("userId", medic.getId());
            startService(intentService);
        }
    }

    public void openMyPatients(View view){
        startActivity(new Intent(this, MyPatients.class));
    }

    public void openCheckPetitions(View view){
        startActivity(new Intent(this, CheckPetitions.class));
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
                intent.putExtra("parent","MedicActivity");
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void logout(){
        stopService(intentService);
        intentService = null;
        firebaseAuth.signOut();
        startActivity(new Intent(this, Login.class));
    }

    @Override
    public void onBackPressed() {
    }
}