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
import com.example.mimedico.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.example.mimedico.utils.ChangeLanguage.changeLanguage;

public class Signup extends AppCompatActivity {

    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText birthdateField;
    private View progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameField = findViewById(R.id.signupUsername);
        emailField = findViewById(R.id.signupEmail);
        birthdateField = findViewById(R.id.signupBirthdate);
        passwordField = findViewById(R.id.signupPassword);
        progressBar = findViewById(R.id.signupProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void changeToLogin(View view) {
        startActivity(new Intent(this, Login.class));
    }

    public void singUp(View view) {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String birthdate = birthdateField.getText().toString().trim();
        if (!validateFields(username, email, birthdate, password)) return;
        progressBar.setVisibility(View.VISIBLE);
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .email(email)
                .birthdate(birthdate)
                .role(Roles.USER.getRole())
                .password(password)
                .build();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            firebaseAuth.getCurrentUser().sendEmailVerification()
                    .addOnSuccessListener(command ->
                            Toast.makeText(getApplicationContext(), "Verification Email has been send", Toast.LENGTH_LONG)
                                    .show())
                    .addOnFailureListener(command ->
                            Toast.makeText(getApplicationContext(), "Cannot Send Email", Toast.LENGTH_LONG)
                                    .show());
            databaseReference.child("users").child(user.getId()).setValue(user);
            startActivity(new Intent(this, EmailNoValidate.class));
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }).addOnCompleteListener(command -> progressBar.setVisibility(View.GONE));
    }


    public boolean validateFields(String username, String email, String birthdate, String password) {
        boolean exit = true;
        if (username.isEmpty()) {
            usernameField.setError("Username is required");
            exit = false;
        }
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
        Pattern DATE_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}$");
        if (!DATE_PATTERN.matcher(birthdate).matches()) {
            birthdateField.setError("Birthdate must be a valid date");
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
                intent.putExtra("parent","SignupActivity");
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
    }
}