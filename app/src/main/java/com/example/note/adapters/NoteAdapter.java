package com.example.note.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.R;
import com.example.note.entities.Note;
import com.example.note.listeners.NoteListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private final Context context;
    private final NoteListener noteListener;

    public NoteAdapter(Context context, List<Note> notes, NoteListener noteListener) {
        this.context = context;
        this.notes = notes;
        this.noteListener = noteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_card, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNoteCard(notes.get(position));
        holder.mainLayout.setOnClickListener(v ->
                noteListener.onNoteClicked(notes.get(position), position));
        holder.mainLayout.setOnLongClickListener(v ->{
                noteListener.onNoteLongClicked(notes.get(position), position, v);
                return true;
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitleText;
        TextView noteContentText;
        TextView noteCreationDateTimeText;

        LinearLayout mainLayout;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleText = itemView.findViewById(R.id.noteTitle);
            noteContentText = itemView.findViewById(R.id.noteContent);
            noteCreationDateTimeText = itemView.findViewById(R.id.noteCreationDateTime);
            mainLayout = itemView.findViewById(R.id.cardNote);

            Animation translateAnim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translateAnim);
        }

        void setNoteCard(Note note){
            noteTitleText.setText(note.getTitle());
            noteContentText.setText(note.getContent());
            noteCreationDateTimeText.setText(note.getCreationDateTime());
        }

    }
}
