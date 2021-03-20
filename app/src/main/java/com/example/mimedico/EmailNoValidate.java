package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class EmailNoValidate extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_no_validate);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void resendEmailVerification(View view) {
        firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnSuccessListener(command ->
                        Toast.makeText(getApplicationContext(), "Verification Email has been send", Toast.LENGTH_LONG).show())
                .addOnFailureListener(command ->
                        Toast.makeText(getApplicationContext(), "Cannot Send Email", Toast.LENGTH_LONG).show());
    }
    
    public void reloadUser(View view){
        firebaseAuth.getCurrentUser().reload();
        if (firebaseAuth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(this, MainActivity.class));
        }else{
            Toast.makeText(getApplicationContext(), "User Not Authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}