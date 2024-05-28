package com.example.kelompok2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String db_notes = "notes_db";
    private static final String tb_notes = "notes";
    private static final String COLUMN_ID = "id";
    private static final String judul = "title";
    private static final String isi = "content";
    private static final String tanggal = "date";

    public DatabaseHelper(Context context) {
        super(context, db_notes, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + tb_notes + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + judul + " TEXT,"
                + isi + " TEXT,"
                + tanggal + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tb_notes);
        onCreate(db);
    }

    public void tambahNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(judul, note.getTitle());
        values.put(isi, note.getContent());
        values.put(tanggal, note.getDate());
        db.insert(tb_notes, null, values);
        db.close();
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tb_notes, new String[]{COLUMN_ID, judul, isi, tanggal}, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(judul)),
                cursor.getString(cursor.getColumnIndexOrThrow(isi)),
                cursor.getString(cursor.getColumnIndexOrThrow(tanggal))
        );
        cursor.close();
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tb_notes;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(judul)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(isi)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(tanggal)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(judul, note.getTitle());
        values.put(isi, note.getContent());
        values.put(tanggal, note.getDate());
        return db.update(tb_notes, values, COLUMN_ID + " = ?", new String[]{String.valueOf(note.getId())});
    }

    public void hapusNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tb_notes, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }


}
