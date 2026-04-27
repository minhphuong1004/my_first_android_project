package com.example.myapplication.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.bumptech.glide.Glide;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


public class ProfileActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
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
        dbHelper = new DatabaseHelper(this);
        email = getIntent().getStringExtra("email");
        if (email != null) {
            Cursor c = dbHelper.getUser(email);

            if (c.moveToFirst()) {
                String name = c.getString(1);
                String emailVal = c.getString(2);
                String address = c.getString(4);
                String avatar = c.getString(5);
                String description = c.getString(6);

                edtName.setText(name);
                edtEmail.setText(emailVal);
                edtAddress.setText(address);
                edtAvatarURL.setText(avatar);
                edtDescription.setText(description);

                txtName.setText(name + "!");

                Glide.with(this)
                        .load(avatar != null && !avatar.isEmpty()
                                ? avatar
                                : R.drawable.default_avatar)
                        .transform(new RoundedCorners(30))
                        .into(imgAvatar);
            }

            c.close();
        }
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);


        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String avatar = edtAvatarURL.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            if (avatar.isEmpty())
                avatar = dbHelper.getAvatar(email);

            boolean success = dbHelper.updateUser(email, name, address, avatar, description);

            if (success) {
                txtName.setText(name + "!");
                Toast.makeText(this, "Save successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("name",name);
                intent.putExtra("avatar",avatar);
                startActivity(intent);
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
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .transform(new RoundedCorners(30))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                            Target<Drawable> target, boolean isFirstResource) {
                                    Log.e("GLIDE", "Load failed: " + url);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target, DataSource dataSource,
                                                               boolean isFirstResource) {
                                    Log.d("GLIDE", "Load success");
                                    return false;
                                }
                            })
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

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String avatar = dbHelper.getAvatar(email);

        Glide.with(this)
                .load(avatar != null && !avatar.isEmpty()
                        ? avatar
                        : R.drawable.default_avatar)
                .transform(new RoundedCorners(30))
                .into(imgAvatar);
    }
}
