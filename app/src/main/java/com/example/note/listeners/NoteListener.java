package com.example.note.listeners;

import android.view.View;

import com.example.note.entities.Note;

public interface NoteListener {
    void onNoteClicked(Note note, int position);
    void onNoteLongClicked(Note note, int position, View view);
}
