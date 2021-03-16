package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mimedico.model.Roles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static com.example.mimedico.utils.ChangeLanguage.changeLanguage;

public class Login extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private View progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginPassword);
        progressBar = findViewById(R.id.loginProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void changeToSignup(View view){
        startActivity(new Intent(this, Signup.class));
    }

    public void login(View view){
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if(!validateFields(email, password)) return;
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(command -> {
                    checkUserTypeAndStartActivity(email);
                })
                .addOnFailureListener(command -> Toast.makeText(getApplicationContext(), "Invalid Data",Toast.LENGTH_LONG).show())
                .addOnCompleteListener(command -> progressBar.setVisibility(View.GONE));
    }

    public void checkUserTypeAndStartActivity(String email){
        firebaseDatabase.getReference("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                String role = "";
                while(iterator.hasNext()){
                    DataSnapshot dataSnapshot1 = iterator.next();
                    role = dataSnapshot1.child("role").getValue().toString();
                }
                if(role.equals(Roles.USER.getRole())){
                    startActivity(new Intent(Login.this, MainActivity.class));
                }else if(role.equals(Roles.MEDIC.getRole())){
                    startActivity(new Intent(Login.this, MainMedic.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public boolean validateFields(String email, String password){
        boolean exit = true;
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            exit = false;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password Is Required");
            exit = false;
        }
        if (password.length() < 6) {
            passwordField.setError("Password leght must be greather than 5");
            exit = false;
        }
        return exit;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.authLanguages:
                Intent intent = new Intent(this, Language.class);
                intent.putExtra("parent","LoginActivity");
                startActivity(intent);
                return true;
        }
        return false;
    }
}