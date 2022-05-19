package com.example.note.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.R;
import com.example.note.entities.Note;
import com.example.note.listeners.NoteListener;
import com.makeramen.roundedimageview.Corner;
import com.makeramen.roundedimageview.RoundedDrawable;
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
        holder.cardLayout.setOnClickListener(v ->
                noteListener.onNoteClicked(notes.get(position), position));
        holder.cardLayout.setOnLongClickListener(v ->{
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

        LinearLayout cardLayout;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleText = itemView.findViewById(R.id.noteTitle);
            noteContentText = itemView.findViewById(R.id.noteContent);
            noteCreationDateTimeText = itemView.findViewById(R.id.noteCreationDateTime);
            cardLayout = itemView.findViewById(R.id.cardNote);
            imageNote = itemView.findViewById(R.id.imageNote);

            Animation translateAnim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            cardLayout.setAnimation(translateAnim);
        }

        void setNoteCard(Note note){
            noteTitleText.setText(note.getTitle());
            noteContentText.setText(note.getContent());
            noteCreationDateTimeText.setText(note.getCreationDateTime());

            if (note.getColor() != null) {
                GradientDrawable gradientDrawable =  new GradientDrawable();
                gradientDrawable.setCornerRadius(25);
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
                cardLayout.setBackground(gradientDrawable);
            }
        }

    }
}
