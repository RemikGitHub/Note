package com.example.note.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.note.DatabaseHelper;
import com.example.note.R;

import java.util.Objects;

public class NoteActivity extends AppCompatActivity {

    private String id;
    private String title;
    private String content;

    private boolean isNewNote;

    private EditText noteTitleEditText;
    private EditText noteContentEditText;
    private TextView noteCreationDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        this.noteTitleEditText = findViewById(R.id.noteTitleEditText);
        this.noteContentEditText = findViewById(R.id.noteContentEditText);
        this.noteCreationDateTime = findViewById(R.id.textDateTime);
        this.isNewNote = true;

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getAndSetIntentData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
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

            case R.id.action_settings:
                confirmDeleteDialog();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete note");
        builder.setMessage("Are you sure you want to delete the \"" + this.title + "\" note?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(NoteActivity.this);
            databaseHelper.deleteNote(this.id);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.create().show();
    }
}