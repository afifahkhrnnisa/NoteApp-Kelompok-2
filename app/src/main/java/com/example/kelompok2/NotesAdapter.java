package com.example.kelompok2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList;
    private List<Note> filteredNoteList;
    private DatabaseHelper db;

    public NotesAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        this.filteredNoteList = new ArrayList<>(noteList);
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = filteredNoteList.get(position);
        holder.txtCatatan.setText(note.getTitle());
        holder.txtTanggal.setText(note.getDate());
        holder.txtIsiCatatan.setText(note.getContent().length() > 20 ? note.getContent().substring(0, 20) + "..." : note.getContent());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Note note = filteredNoteList.get(position);
                    Intent intent = new Intent(context, AddEdit.class);
                    intent.putExtra("noteId", note.getId());
                    ((Activity) context).startActivityForResult(intent, 100);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createDialog();
                dialog.show();
            }
                AlertDialog createDialog () {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Warning");
                    builder.setMessage("Do you want to delete this ??");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            Toast.makeText(context, "Notes have been Deleted", Toast.LENGTH_LONG).show();
                            int position = holder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                Note note = filteredNoteList.get(position);
                                db.hapusNote(note.getId());
                                int originalPosition = noteList.indexOf(note);
                                noteList.remove(note);
                                filteredNoteList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, filteredNoteList.size());
                                notifyItemRemoved(originalPosition);
                                notifyItemRangeChanged(originalPosition, noteList.size());
                            }
                        }

                    });
                    return builder.show();
                }
        });
    }

    @Override
    public int getItemCount() {
        return filteredNoteList.size();
    }

    public void filterNotes(String query) {
        filteredNoteList.clear();
        if (query.isEmpty()) {
            filteredNoteList.addAll(noteList);
        } else {
            for (Note note : noteList) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getContent().toLowerCase().contains(query.toLowerCase())) {
                    filteredNoteList.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCatatan, txtTanggal, txtIsiCatatan;
        public ImageButton btnEdit, btnDelete;

        public NoteViewHolder(View itemView) {
            super(itemView);
            txtCatatan = itemView.findViewById(R.id.txtCatatan);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtIsiCatatan = itemView.findViewById(R.id.txtIsiCatatan);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void updateFilteredNoteList(List<Note> newNotes) {
        this.filteredNoteList.clear();
        this.filteredNoteList.addAll(newNotes);
    }


}
