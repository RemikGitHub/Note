package com.example.note.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private final List<Note> notes;
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
        holder.cardLayout.setOnLongClickListener(v -> {
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
        private final TextView noteTitleText;
        private final TextView noteContentText;
        private final TextView noteCreationDateTimeText;

        private final LinearLayout cardLayout;
        private final RoundedImageView imageNote;

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

        void setNoteCard(Note note) {
            noteTitleText.setText(note.getTitle());
            noteContentText.setText(note.getContent());
            noteCreationDateTimeText.setText(note.getCreationDateTime());

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(43);
            gradientDrawable.setColor(Color.parseColor(note.getColor()));

            GradientDrawable gradientDrawableClick = new GradientDrawable();
            gradientDrawableClick.setCornerRadius(43);
            gradientDrawableClick.setColor(Color.parseColor("#CCCCCC"));

            StateListDrawable res = new StateListDrawable();
            res.setExitFadeDuration(400);

            res.addState(new int[]{android.R.attr.state_pressed}, gradientDrawableClick);
            res.addState(new int[]{}, gradientDrawable);
            cardLayout.setBackground(res);

            if (note.getImagePath() != null && !note.getImagePath().trim().isEmpty()) {
                Picasso.get().load(new File(note.getImagePath())).resize(800, 0).into(imageNote);
                imageNote.setVisibility(View.VISIBLE);
            } else {
                imageNote.setVisibility(View.GONE);
            }

        }

    }
}
