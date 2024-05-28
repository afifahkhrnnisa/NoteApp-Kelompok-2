package com.example.kelompok2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private DatabaseHelper db;
    private ImageButton btnTambah;
    private EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        btnTambah = findViewById(R.id.btnTambah);
        inputSearch = findViewById(R.id.inputSearch);

        noteList.addAll(db.getAllNotes());

        adapter = new NotesAdapter(this, noteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEdit.class);
                startActivityForResult(intent, 1);
            }
        });


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            noteList.clear();
            noteList.addAll(db.getAllNotes());
            adapter.updateFilteredNoteList(noteList);
            adapter.notifyDataSetChanged();
        }
        else if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            int noteId = data.getIntExtra("noteId", -1);
            if (noteId != -1) {
                Note updatedNote = db.getNote(noteId);
                for (int i = 0; i < noteList.size(); i++) {
                    if (noteList.get(i).getId() == noteId) {
                        noteList.set(i, updatedNote);
                        break;
                    }
                }
                adapter.updateFilteredNoteList(noteList);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
