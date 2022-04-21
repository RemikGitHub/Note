package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NoteActivity extends AppCompatActivity {

    private String id;
    private String title;
    private String content;

    private boolean isNewNote;

    private EditText noteTitleEditText;
    private EditText noteContentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.noteTitleEditText = findViewById(R.id.noteTitleEditText);
        this.noteContentEditText = findViewById(R.id.noteContentEditText);
        this.isNewNote = true;

        getAndSetIntentData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveNote) {

            DatabaseHelper databaseHelper = new DatabaseHelper(NoteActivity.this);
            this.title = noteTitleEditText.getText().toString().trim();
            this.content = noteContentEditText.getText().toString().trim();

            if (this.isNewNote) {
                this.isNewNote = false;
                this.id = String.valueOf(databaseHelper.addNote(this.title, this.content));
            } else {
                databaseHelper.updateNote(this.id, this.title, this.content);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("content")) {

            this.isNewNote = false;

            this.id = getIntent().getStringExtra("id");
            this.title = getIntent().getStringExtra("title");
            this.content = getIntent().getStringExtra("content");

            noteTitleEditText.setText(title);
            noteContentEditText.setText(content);

        }
    }

}