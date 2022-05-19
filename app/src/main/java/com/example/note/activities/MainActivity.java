package com.example.note.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
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
    public static final int REQUEST_CODE_DELETE_NOTE = 3;
    public static final int REQUEST_CODE_DELETE_NEW_NOTE = 4;

    private RecyclerView recyclerView;
    private List<Note> notes;
    private NoteAdapter noteAdapter;
    private FloatingActionButton addButton;
    private ImageView emptyImage;
    private TextView emptyText;
    private EditText searchInput;
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
                    case REQUEST_CODE_DELETE_NEW_NOTE: {
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
        searchInput = findViewById(R.id.searchInput);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteActivity.class);
            noteActivityResultLauncherAddNote.launch(intent);
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ArrayList<Note> notesFromDb = (ArrayList<Note>) NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().getAllNotes();

            handler.post(() -> {
                notes = new ArrayList<>(notesFromDb);

                noteAdapter = new NoteAdapter(MainActivity.this, notes, this);
                noteAdapter.notifyItemRangeChanged(0, notes.size());

                if (notes.size() == 0) {
                    showEmptyContent();
                } else {
                    hideEmptyContent();
                }

                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setAdapter(noteAdapter);

                noteAdapter.getFilter().filter(searchInput.getText().toString());

                searchInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        noteAdapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            });
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchNote = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchNote.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);

                return false;
            }
        });
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

        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.layout_delete_all_notes_dialog, findViewById(R.id.layoutDeleteNoteContainer));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);

        AlertDialog dialogDeleteAllNotes = builder.create();

        if (dialogDeleteAllNotes.getWindow() != null) {
            dialogDeleteAllNotes.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialogLayout.findViewById(R.id.textDeleteNote).setOnClickListener(v -> {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().deleteAllNotes();

                handler.post(() -> {
                    dialogDeleteAllNotes.dismiss();
                    Toast.makeText(this, "Deleted all notes", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            });
        });
        dialogLayout.findViewById(R.id.deleteNoteCancel).setOnClickListener(v -> dialogDeleteAllNotes.dismiss());

        dialogDeleteAllNotes.show();
    }

    private void getNotes(final int requestCode) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ArrayList<Note> notesFromDb = (ArrayList<Note>) NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().getAllNotes();

            handler.post(() -> {
                if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    hideEmptyContent();
                    noteAdapter.addNote(0, notesFromDb.get(0));
                    recyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteAdapter.updateNote(noteChosenPosition, notesFromDb);
                } else if (requestCode == REQUEST_CODE_DELETE_NOTE) {
                    noteAdapter.deleteNote(noteChosenPosition);
                    if (noteAdapter.thereAreNoNotes()) {
                        showEmptyContent();
                    }
                }
                noteAdapter.getFilter().filter(searchInput.getText().toString());
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

            View dialogLayout = LayoutInflater.from(this).inflate(R.layout.layout_delete_note_dialog, findViewById(R.id.layoutDeleteNoteContainer));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogLayout);

            AlertDialog dialogDeleteNote = builder.create();

            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            dialogLayout.findViewById(R.id.textDeleteNote).setOnClickListener(v -> {

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().deleteNote(note);

                    handler.post(() -> {
                        getNotes(REQUEST_CODE_DELETE_NOTE);
                        dialogDeleteNote.dismiss();
                    });
                });
            });
            dialogLayout.findViewById(R.id.deleteNoteCancel).setOnClickListener(v -> dialogDeleteNote.dismiss());

            dialogDeleteNote.show();

            return true;
        });
        popupMenu.show();
    }
}