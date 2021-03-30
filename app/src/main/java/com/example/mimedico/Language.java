package com.example.mimedico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.mimedico.utils.ChangeLanguage;
import android.view.View;

public class Language extends AppCompatActivity {

    private Button spanishButton;
    private Button englishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        spanishButton = findViewById(R.id.spanishButton);
        englishButton = findViewById(R.id.englishButton);
    }

    public void setSpanishAsLanguage(View view){
        ChangeLanguage.changeLanguage(this, "es");
        this.recreate();
    }

    public void setEnglishAsLanguage(View view){
        ChangeLanguage.changeLanguage(this, "en");
        this.recreate();
    }

    public void onBackPressed() {
        if(getIntent().getStringExtra("parent").equals("MainActivity")) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (getIntent().getStringExtra("parent").equals("LoginActivity")) {
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (getIntent().getStringExtra("parent").equals("SignupActivity")) {
            Intent intent = new Intent(this, Signup.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (getIntent().getStringExtra("parent").equals("MedicActivity")) {
            Intent intent = new Intent(this, MainMedic.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if(getIntent().getStringExtra("parent").equals("EmailNotValidateActivity")){
            Intent intent = new Intent(this, EmailNoValidate.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(getIntent().getStringExtra("parent").equals("MainAdmin")){
            Intent intent = new Intent(this, MainAdmin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {
            super.onBackPressed();
        }
    }
}