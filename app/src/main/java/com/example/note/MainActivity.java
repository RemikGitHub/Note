package com.example.note;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private ImageView emptyImage;
    private TextView emptyText;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> noteIds;
    private ArrayList<String> noteTitles;
    private ArrayList<String> noteContents;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        emptyImage = findViewById(R.id.empty_image);
        emptyText = findViewById(R.id.empty_text);

        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteActivity.class);
            startActivity(intent);
        });

        databaseHelper = new DatabaseHelper(this);

        noteIds = new ArrayList<>();
        noteTitles = new ArrayList<>();
        noteContents = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this, noteIds, noteTitles, noteContents);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                confirmDeleteDialog();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void storeDataInArrays() {
        Cursor cursor = databaseHelper.readAllNotes();

        if (cursor.getCount() == 0) {
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {

            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);

            while (cursor.moveToNext()) {
                noteIds.add(cursor.getString(0));
                noteTitles.add(cursor.getString(1));
                noteContents.add(cursor.getString(2));
            }
        }
        cursor.close();
    }

    private void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete notes");
        builder.setMessage("Are you sure you want to delete all notes?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            databaseHelper.deleteAllNotes();
            Toast.makeText(this, "Deleted all notes", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.create().show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}