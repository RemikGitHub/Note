package com.example.note;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.activities.NoteActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> noteIds;
    private final ArrayList<String> noteTitles;
    private final ArrayList<String> noteContents;

    CustomAdapter(Context context, ArrayList<String> noteIds, ArrayList<String> noteTitles, ArrayList<String> noteContents) {
        this.context = context;
        this.noteIds = noteIds;
        this.noteTitles = noteTitles;
        this.noteContents = noteContents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitleText.setText(noteTitles.get(position));
        holder.noteContentText.setText(noteContents.get(position));

        holder.mainLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("id", noteIds.get(position));
            intent.putExtra("title", noteTitles.get(position));
            intent.putExtra("content", noteContents.get(position));
            context.startActivity(intent);
        });

        holder.mainLayout.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete note");
                builder.setMessage("Are you sure you want to delete the \"" + noteTitles.get(position) + "\" note?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    databaseHelper.deleteNote(noteIds.get(position));

                    noteIds.remove(position);
                    noteTitles.remove(position);
                    noteContents.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged( position, noteIds.size());

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                builder.create().show();

                return true;
            });
            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitleText;
        TextView noteContentText;
        LinearLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleText = itemView.findViewById(R.id.noteTitle);
            noteContentText = itemView.findViewById(R.id.noteContent);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            Animation translateAnim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translateAnim);
        }
    }
}
