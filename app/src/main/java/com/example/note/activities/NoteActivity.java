package com.example.note.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.note.R;
import com.example.note.database.NoteDatabase;
import com.example.note.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private View titleIndicator;

    private String selectedNoteColor;


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
        this.titleIndicator = findViewById(R.id.titleIndicator);

        this.isNewNote = getIntent().getBooleanExtra("isNewNote", true);

        setupActivity();
        initOptions();
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
        if (!this.isNewNote) {
            this.note = (Note) getIntent().getSerializableExtra("note");

            noteTitleEditText.setText(note.getTitle());
            noteContentEditText.setText(note.getContent());
            noteCreationDateTime.setText(note.getCreationDateTime());

        } else {
            this.note = new Note();

            noteCreationDateTime.setText(new SimpleDateFormat(
                    "HH:mm - EEEE, dd MMMM yyyy", Locale.getDefault()).format(new Date().getTime())
            );
        }
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

        note.setTitle(noteTitle);
        note.setContent(noteText);
        note.setCreationDateTime(noteDateTime);
        note.setColor(selectedNoteColor);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            long newId = NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().insertNote(note);

            handler.post(() -> {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                if (isNewNote) {
                    setResult(MainActivity.REQUEST_CODE_ADD_NOTE, intent);
                    note.setId(newId);
                } else {
                    setResult(MainActivity.REQUEST_CODE_UPDATE_NOTE, intent);
                }

            });
        });
    }

    private void setTitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) titleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    private void setImageViewsColor(String color, int chosenColor) {
        final LinearLayout layoutOptions = findViewById(R.id.layoutOptions);

        final ImageView imageColorDefault = layoutOptions.findViewById(R.id.imageColorDefault);
        final ImageView imageColorYellow = layoutOptions.findViewById(R.id.imageColorYellow);
        final ImageView imageColorRed = layoutOptions.findViewById(R.id.imageColorRed);
        final ImageView imageColorBlue = layoutOptions.findViewById(R.id.imageColorBlue);
        final ImageView imageColorBlack = layoutOptions.findViewById(R.id.imageColorBlack);

        selectedNoteColor = color;

        imageColorDefault.setImageResource(0);
        imageColorYellow.setImageResource(0);
        imageColorRed.setImageResource(0);
        imageColorBlue.setImageResource(0);
        imageColorBlack.setImageResource(0);

        switch (chosenColor) {
            case 0: {
                imageColorDefault.setImageResource(R.drawable.ic_done);
                break;
            }
            case 1: {
                imageColorYellow.setImageResource(R.drawable.ic_done);
                break;
            }
            case 2: {
                imageColorRed.setImageResource(R.drawable.ic_done);
                break;
            }
            case 3: {
                imageColorBlue.setImageResource(R.drawable.ic_done);
                break;
            }
            case 4: {
                imageColorBlack.setImageResource(R.drawable.ic_done);
                break;
            }
        }
        setTitleIndicatorColor();
    }

    private void initOptions() {
        final LinearLayout layoutOptions = findViewById(R.id.layoutOptions);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutOptions);
        layoutOptions.findViewById(R.id.textOptions).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        layoutOptions.findViewById(R.id.viewColorDefault).setOnClickListener(v -> setImageViewsColor("#333333", 0));
        layoutOptions.findViewById(R.id.viewColorYellow).setOnClickListener(v -> setImageViewsColor("#FDBE3B", 1));
        layoutOptions.findViewById(R.id.viewColorRed).setOnClickListener(v -> setImageViewsColor("#FF4842", 2));
        layoutOptions.findViewById(R.id.viewColorBlue).setOnClickListener(v -> setImageViewsColor("#3A52FC", 3));
        layoutOptions.findViewById(R.id.viewColorBlack).setOnClickListener(v -> setImageViewsColor("#000000", 4));

//        if (alreadyAvailableNote != null) {
//            final String noteColorCode = alreadyAvailableNote.getColor();
//            if (noteColorCode != null && !noteColorCode.trim().isEmpty()) {
//                switch (noteColorCode) {
//                    case "#FDBE3B":
//                        layoutOptions.findViewById(R.id.viewColorYellow).performClick();
//                        break;
//                    case "#FF4842":
//                        layoutOptions.findViewById(R.id.viewColorRed).performClick();
//                        break;
//                    case "#3A52FC":
//                        layoutOptions.findViewById(R.id.viewColorBlue).performClick();
//                        break;
//                    case "#000000":
//                        layoutOptions.findViewById(R.id.viewColorBlack).performClick();
//                        break;
//                }
//            }
//        }
//
//        layoutOptions.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(CreateNoteActivity.this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
//            } else {
//                selectImage();
//            }
//        });
//
//        layoutOptions.findViewById(R.id.layoutAddUrl).setOnClickListener(v -> {
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            showAddURLDialog();
//        });
//
//        if (alreadyAvailableNote != null) {
//            layoutOptions.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
//            layoutOptions.findViewById(R.id.layoutDeleteNote).setOnClickListener(v -> {
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                showDeleteNoteDialog();
//            });
//        }
    }

}