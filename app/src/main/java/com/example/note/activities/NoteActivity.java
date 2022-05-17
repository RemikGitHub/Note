package com.example.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.note.R;
import com.example.note.database.NoteDatabase;
import com.example.note.entities.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {

    private boolean isNewNote;
    private Note note;

    private EditText noteTitleEditText;
    private EditText noteContentEditText;
    private TextView noteCreationDateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        this.noteTitleEditText = findViewById(R.id.noteTitleEditText);
        this.noteContentEditText = findViewById(R.id.noteContentEditText);
        this.noteCreationDateTime = findViewById(R.id.textDateTime);

        this.isNewNote = getIntent().getBooleanExtra("isNewNote", true);

        setupActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.save_note) {
            saveNote();

            return true;
        }
        if (itemId == R.id.action_settings) {
            confirmDeleteDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void setupActivity() {
        if (!getIntent().getBooleanExtra("isNewNote", true)) {

            this.isNewNote = false;

            this.note = (Note) getIntent().getSerializableExtra("note");

            noteTitleEditText.setText(note.getTitle());
            noteContentEditText.setText(note.getContent());
            noteCreationDateTime.setText(note.getCreationDateTime());

        }

        noteCreationDateTime.setText(new SimpleDateFormat(
                "HH:mm - EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date().getTime())
        );
    }

    private void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete note");
        builder.setMessage("Are you sure you want to delete the \"" + this.note.getTitle() + "\" note?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().deleteNote(note);

                handler.post(() -> {
                    Intent intent = new Intent();
                    setResult(MainActivity.REQUEST_CODE_DELETE_NOTE, intent);
                    finish();
                });
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.create().show();
    }

    private void saveNote() {
        final String noteTitle = noteTitleEditText.getText().toString();
        final String noteText = noteContentEditText.getText().toString();
        final String noteDateTime = noteCreationDateTime.getText().toString();

        final Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteText);
        note.setCreationDateTime(noteDateTime);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().insertNote(note);

            handler.post(() -> {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                if (isNewNote) {
                    setResult(MainActivity.REQUEST_CODE_ADD_NOTE, intent);
                    isNewNote = false;
                } else {
                    setResult(MainActivity.REQUEST_CODE_UPDATE_NOTE, intent);
                }

            });
        });
    }
}