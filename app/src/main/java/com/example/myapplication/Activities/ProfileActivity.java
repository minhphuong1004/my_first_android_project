package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.bumptech.glide.Glide;


public class ProfileActivity extends AppCompatActivity {
    TextView txtName;
    TextInputEditText edtName,edtEmail, edtAddress, edtAvatarURL, edtDescription;
    String email;
    Button btnSave, btnLogout;
    ImageView imgAvatar;
    Handler handler = new Handler();
    Runnable runnable;


    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        txtName = findViewById(R.id.txtName);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtAvatarURL = findViewById(R.id.edtAvatarURL);
        edtDescription = findViewById(R.id.edtDescription);
        imgAvatar = findViewById(R.id.imgAvatar);
        Glide.with(this)
                .load(R.drawable.default_avatar)
                .transform(new RoundedCorners(30))
                .into(imgAvatar);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        email = getIntent().getStringExtra("email");
        if (email != null) {
            String name = dbHelper.getName(email);
            edtName.setText(name);
            txtName.setText(name + "!");
        }
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);


        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String avatar = edtAvatarURL.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            boolean success = dbHelper.updateUser(email, name, address, avatar, description);

            if (success) {
                txtName.setText(name + "!");
                Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                txtName.setText(s.toString() + "!");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });




        edtAvatarURL.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                if (runnable != null) handler.removeCallbacks(runnable);

                runnable = () -> {
                    String url = s.toString().trim();

                    Glide.with(ProfileActivity.this)
                            .load(url.isEmpty() ? R.drawable.default_avatar : url)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .transform(new RoundedCorners(30))
                            .into(imgAvatar);
                };

                handler.postDelayed(runnable, 500);
            }
        });



        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
