package com.example.note;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
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

    private void storeDataInArrays() {
        Cursor cursor = databaseHelper.readAllNotes();

        if (cursor.getCount() == 0)
            Toast.makeText(this, "There is no notes", Toast.LENGTH_SHORT).show();
        else {
            while (cursor.moveToNext()) {
                noteIds.add(cursor.getString(0));
                noteTitles.add(cursor.getString(1));
                noteContents.add(cursor.getString(2));
            }
        }
        cursor.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}