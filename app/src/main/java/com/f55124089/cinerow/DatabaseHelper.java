package com.f55124089.cinerow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "XX2_Cinema.db";
    private static final int DATABASE_VERSION = 1;

    // Nama Tabel & Kolom
    private static final String TABLE_FAVORITE = "table_favorite";
    private static final String KEY_ID = "id";
    private static final String KEY_JUDUL = "judul";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_POSTER = "poster";
    private static final String KEY_DESKRIPSI = "deskripsi";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel otomatis saat aplikasi pertama kali dijalankan
        String CREATE_TABLE = "CREATE TABLE " + TABLE_FAVORITE + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_JUDUL + " TEXT,"
                + KEY_GENRE + " TEXT,"
                + KEY_POSTER + " TEXT,"
                + KEY_DESKRIPSI + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        onCreate(db);
    }

    // 1. FUNGSI MENAMBAH FILM KE FAVORIT
    public void addFavorite(Film film) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, film.getId());
        values.put(KEY_JUDUL, film.getJudul());
        values.put(KEY_GENRE, film.getGenre());
        values.put(KEY_POSTER, film.getPoster());
        values.put(KEY_DESKRIPSI, film.getDeskripsi());

        db.insert(TABLE_FAVORITE, null, values);
        db.close();
    }

    // 2. FUNGSI MENGHAPUS FILM DARI FAVORIT
    public void removeFavorite(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITE, KEY_ID + " = ?", new String[]{id});
        db.close();
    }

    // 3. FUNGSI CEK STATUS (Apakah film ini sudah masuk favorit?)
    public boolean isFavorite(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITE, new String[]{KEY_ID},
                KEY_ID + "=?", new String[]{id}, null, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // 4. FUNGSI AMBIL SEMUA DAFTAR FILM FAVORIT
    public List<Film> getAllFavorites() {
        List<Film> favoriteList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Film film = new Film(
                        cursor.getString(1), // judul
                        cursor.getString(2), // genre
                        cursor.getString(3), // poster
                        cursor.getString(4)  // deskripsi
                );
                film.setId(cursor.getString(0)); // id
                favoriteList.add(film);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
}