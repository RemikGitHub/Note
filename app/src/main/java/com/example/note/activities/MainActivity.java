package com.example.note.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.note.DatabaseHelper;
import com.example.note.R;
import com.example.note.adapters.NoteAdapter;
import com.example.note.database.NoteDatabase;
import com.example.note.entities.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Note> notes;
    private NoteAdapter noteAdapter;

    private FloatingActionButton addButton;
    private ImageView emptyImage;
    private TextView emptyText;

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTES = 3;
    public static final int REQUEST_CODE_DELETE_NOTE = 4;

    private int noteChosenPosition = -1;

    private final ActivityResultLauncher<Intent> noteActivityResultLauncherAddNote = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()){
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
        noteAdapter = new NoteAdapter(MainActivity.this, notes);

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
        switch (item.getItemId()) {
            case R.id.delete_all:
                confirmDeleteDialog();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void storeDataInArrays() {
//        Cursor cursor = databaseHelper.readAllNotes();
//
//        if (cursor.getCount() == 0) {
//            emptyImage.setVisibility(View.VISIBLE);
//            emptyText.setVisibility(View.VISIBLE);
//        } else {
//
//            emptyImage.setVisibility(View.GONE);
//            emptyText.setVisibility(View.GONE);
//
//            while (cursor.moveToNext()) {
//                noteIds.add(cursor.getString(0));
//                noteTitles.add(cursor.getString(1));
//                noteContents.add(cursor.getString(2));
//            }
//        }
//        cursor.close();
//    }

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

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//
//        this.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//        startActivity(new Intent(this, MainActivity.class));
//        finish();
//    }

    private void getNotes(final int requestCode) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            ArrayList<Note> notesFromDb = (ArrayList<Note>) NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao().getAllNotes();

            handler.post(() -> {
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    notes.addAll(notesFromDb);
                    noteAdapter.notifyDataSetChanged();

                    if (notes.size() == 0){
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }
                }
                else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    emptyImage.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);

                    notes.add(0, notesFromDb.get(0));
                    noteAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                }
                else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    notes.remove(noteChosenPosition);
                    notes.add(noteChosenPosition, notes.get(noteChosenPosition));
                    noteAdapter.notifyItemChanged(noteChosenPosition);
                }
                else if (requestCode == REQUEST_CODE_DELETE_NOTE) {
                    notes.remove(noteChosenPosition);
                    noteAdapter.notifyItemRemoved(noteChosenPosition);
                }
            });
        });
    }
}