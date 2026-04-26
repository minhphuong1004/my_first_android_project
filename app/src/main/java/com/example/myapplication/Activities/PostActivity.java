package com.example.myapplication.Activities;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Post;
import com.example.myapplication.adapter.PostAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {
    ArrayList<Post> postList;
    PostAdapter adapter;
    ListView listView;
    EditText edtPost;
    Button btnPost;
    String email;
    String userName;
    String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.listPost);
        edtPost = findViewById(R.id.edtPost);
        btnPost = findViewById(R.id.btnPost);

        //data
        email = getIntent().getStringExtra("email");
        userName = getIntent().getStringExtra("name");
        DatabaseHelper db = new DatabaseHelper(this);
        avatarUrl = db.getAvatar(email);
        postList = db.getPosts(email);

        //adapter
        adapter = new PostAdapter(this, postList, avatarUrl);
        listView.setAdapter(adapter);

        //context menu
        registerForContextMenu(listView);
        listView.setOnItemLongClickListener((parent, view, position, id) -> false);

        //post btn
        btnPost.setOnClickListener(v -> {
            String content = edtPost.getText().toString().trim();

            if (content.isEmpty()) return;

            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date());

            String name = (userName != null) ?userName : "You";

            //luu DB
            db.insertPost(email, content, date);

            //add UI
            postList.add(0, new Post(name, content, date));
            adapter.notifyDataSetChanged();

            edtPost.setText("");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper db = new DatabaseHelper(this);
        avatarUrl = db.getAvatar(email);

        adapter = new PostAdapter(this, postList, avatarUrl);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        Toast.makeText(this, "Menu created", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
            intent.putExtra("email", getIntent().getStringExtra("email"));
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_sort_date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Collections.sort(postList, (p1, p2) -> {
                try {
                    Date d1 = sdf.parse(p1.date);
                    Date d2 = sdf.parse(p2.date);
                    return d2.compareTo(d1); // mới nhất lên đầu
                } catch (Exception e) {
                    return 0;
                }
            });
            adapter.notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.action_sort_name) {
            Collections.sort(postList, (p1, p2) ->
                    p1.title.compareToIgnoreCase(p2.title));
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int position = info.position;

        if (item.getItemId() == R.id.action_hide) {
            postList.remove(position);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }
}
