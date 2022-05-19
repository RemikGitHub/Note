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
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {
    public List<Note> notesBackup;
    private final List<Note> notes;
    private final Context context;
    private final NoteListener noteListener;
    private int absolutePosition;

    public NoteAdapter(Context context, List<Note> notes, NoteListener noteListener) {
        this.context = context;
        this.noteListener = noteListener;
        this.notes = notes;
        this.notesBackup = new ArrayList<>(notes);
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

        holder.cardLayout.setOnClickListener(v -> {
            absolutePosition = notesBackup.indexOf(notes.get(position));
            noteListener.onNoteClicked(notes.get(position), position);
        });

        holder.cardLayout.setOnLongClickListener(v -> {
            absolutePosition = notesBackup.indexOf(notes.get(position));
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

    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private final Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(notesBackup);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note note : notesBackup) {
                    if (note.getTitle().toLowerCase().contains(filterPattern) ||
                            note.getContent().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((List<Note>) results.values);
            notifyDataSetChanged();
        }
    };

    public void deleteNote(int position) {
        notesBackup.remove(absolutePosition);
        notes.remove(position);

        notifyItemRemoved(position);
    }

    public void updateNote(int position, List<Note> notesFromDb) {
        Note changedNote = notesFromDb.get(absolutePosition);
        notesBackup.set(absolutePosition, changedNote);
        notes.set(position, changedNote);

        notifyItemChanged(position);
    }

    public void addNote(int position, Note note) {
        notesBackup.add(0, note);
        notes.add(0, note);
        notifyItemInserted(position);
    }

    public boolean thereAreNoNotes() {
        return notesBackup.isEmpty();
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
