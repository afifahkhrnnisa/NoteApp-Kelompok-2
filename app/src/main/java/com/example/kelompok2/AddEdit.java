package com.example.kelompok2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEdit extends AppCompatActivity {
    private ImageButton btnKembali, btnSimpan, btnShare;
    private EditText editJudul, editCatatan;
    private TextView txtTgl;
    private DatabaseHelper db;
    private boolean isEdit = false;
    private int noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_edit);

        db = new DatabaseHelper(this);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnShare = findViewById(R.id.btnShare);
        txtTgl = findViewById(R.id.txtTgl);
        editJudul = findViewById(R.id.editJudul);
        editCatatan = findViewById(R.id.editCatatan);
        btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("noteId")) {
            noteId = intent.getIntExtra("noteId", -1);
            isEdit = true;
            loadNoteData(noteId);
        }
        else {
            txtTgl.setText(
                    new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                            .format(new Date())
            );
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = editJudul.getText().toString();
                String catatan = editCatatan.getText().toString();
                String bagikan = "Judul: " + judul + "\nCatatan: " + catatan;

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, bagikan);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Bagikan catatan via"));
            }
        });
    }

    private void loadNoteData(int noteId) {
        Note note = db.getNote(noteId);
        editJudul.setText(note.getTitle());
        editCatatan.setText(note.getContent());
        txtTgl.setText(note.getDate());
    }

    private void simpan() {
        String title = editJudul.getText().toString().trim();
        String content = editCatatan.getText().toString().trim();
        String date = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());


        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Mohon Isi Semua Bagian", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setDate(date);

        if (isEdit) {
            note.setId(noteId);
            db.updateNote(note);
        } 
        else {
            db.tambahNote(note);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteId", note.getId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateTanggal() {
        txtTgl.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
    }
}
