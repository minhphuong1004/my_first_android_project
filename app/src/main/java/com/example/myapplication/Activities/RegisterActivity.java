package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        EditText edtName1, edtEmail1, edtPw1, confirmPw1;
        edtName1 = findViewById(R.id.edtName1);
        edtEmail1 = findViewById(R.id.edtEmail1);
        edtPw1 = findViewById(R.id.edtPw1);
        confirmPw1 = findViewById(R.id.confirmPw1);

        Button btnCreate = findViewById(R.id.btnCreate);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        btnCreate.setOnClickListener(v -> {
            String name = edtName1.getText().toString();
            String email = edtEmail1.getText().toString();
            String password = edtPw1.getText().toString();
            String confirm = confirmPw1.getText().toString();

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Cannot be left blank!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertUser(name,email,password);
            if (success) {
                Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "This Email is already exist", Toast.LENGTH_SHORT).show();
            }


        });

    }


}
