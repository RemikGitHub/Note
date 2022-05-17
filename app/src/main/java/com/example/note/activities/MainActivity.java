package com.example.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.note.R;
import com.example.note.adapters.NoteAdapter;
import com.example.note.database.NoteDatabase;
import com.example.note.entities.Note;
import com.example.note.listeners.NoteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NoteListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;
    public static final int REQUEST_CODE_DELETE_NOTE = 4;
    private RecyclerView recyclerView;
    private List<Note> notes;
    private NoteAdapter noteAdapter;
    private FloatingActionButton addButton;
    private ImageView emptyImage;
    private TextView emptyText;
    private int noteChosenPosition = -1;

    private final ActivityResultLauncher<Intent> noteActivityResultLauncherAddNote = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case REQUEST_CODE_ADD_NOTE: {
                        getNotes(REQUEST_CODE_ADD_NOTE);
                        break;
                    }
                    case REQUEST_CODE_UPDATE_NOTE: {
                        getNotes(REQUEST_CODE_UPDATE_NOTE);
                        break;
                    }
                    case REQUEST_CODE_DELETE_NOTE: {
                        getNotes(REQUEST_CODE_DELETE_NOTE);
                        break;
                    }
                }
            });

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
            noteActivityResultLauncherAddNote.launch(intent);
        });

        notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(MainActivity.this, notes, this);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(noteAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDeleteDialog();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete notes");
        builder.setMessage("Are you sure you want to delete all notes?");
        builder.setPositiveButton("Yes", (dialog, which) -> {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().deleteAllNotes();

                handler.post(() -> {
                    Toast.makeText(this, "Deleted all notes", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.create().show();
    }

    private void getNotes(final int requestCode) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ArrayList<Note> notesFromDb = (ArrayList<Note>) NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().getAllNotes();

            handler.post(() -> {
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    notes.addAll(notesFromDb);
                    noteAdapter.notifyItemRangeChanged(0, notes.size());

                    if (notes.size() == 0) {
                        showEmptyContent();
                    } else {
                        hideEmptyContent();
                    }
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    hideEmptyContent();

                    notes.add(0, notesFromDb.get(0));
                    noteAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    notes.remove(noteChosenPosition);
                    notes.add(noteChosenPosition, notesFromDb.get(noteChosenPosition));
                    noteAdapter.notifyItemChanged(noteChosenPosition);
                } else if (requestCode == REQUEST_CODE_DELETE_NOTE) {
                    notes.remove(noteChosenPosition);
                    noteAdapter.notifyItemRemoved(noteChosenPosition);

                    if (notes.size() == 0) {
                        showEmptyContent();
                    }
                }
            });
        });
    }

    private void showEmptyContent() {
        emptyImage.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
    }

    private void hideEmptyContent() {
        emptyImage.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteChosenPosition = position;

        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.putExtra("isNewNote", false);
        intent.putExtra("note", note);

        noteActivityResultLauncherAddNote.launch(intent);
    }

    @Override
    public void onNoteLongClicked(Note note, int position, View view) {
        noteChosenPosition = position;

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Delete note");
            builder.setMessage("Are you sure you want to delete the \"" + note.getTitle() + "\" note?");
            builder.setPositiveButton("Yes", (dialog, which) -> {

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().deleteNote(note);

                    handler.post(() -> getNotes(REQUEST_CODE_DELETE_NOTE));
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> {
            });
            builder.create().show();

            return true;
        });
        popupMenu.show();
    }
}