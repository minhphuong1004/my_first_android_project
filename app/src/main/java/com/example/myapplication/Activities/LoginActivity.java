package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         EditText inputEmail, inputPassword;
         inputEmail = findViewById(R.id.inputEmail);
         inputPassword = findViewById(R.id.inputPassword);
         Button btnRegister = findViewById(R.id.btnRegister);
         btnRegister.setOnClickListener(v -> {
             Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
             startActivity(intent);
         });

         DatabaseHelper dbHelper = new DatabaseHelper(this);

         Button btnSignIn = findViewById(R.id.btnSignIn);
         btnSignIn.setOnClickListener(v -> {
             String email = inputEmail.getText().toString();
             String password = inputPassword.getText().toString();

             boolean isValid = dbHelper.checkUser(email, password);
             if (isValid) {
                 Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                 intent.putExtra("email", email);
                 startActivity(intent);
             } else {
                 Toast.makeText(this, "Wrong information", Toast.LENGTH_SHORT).show();
             }

         });

    }
}