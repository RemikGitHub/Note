package com.example.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> noteTitles;
    private final ArrayList<String> noteContents;

    CustomAdapter(Context context, ArrayList<String> noteTitles, ArrayList<String> noteContents) {
        this.context = context;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.noteTitleText.setText(noteTitles.get(position));
        holder.noteContentText.setText(noteContents.get(position));
    }

    @Override
    public int getItemCount() {
        return noteTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitleText;
        TextView noteContentText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleText = itemView.findViewById(R.id.noteTitle);
            noteContentText = itemView.findViewById(R.id.noteContent);
        }
    }
}
