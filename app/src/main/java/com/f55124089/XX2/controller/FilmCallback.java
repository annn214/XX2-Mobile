package com.f55124089.XX2.controller;

import com.f55124089.XX2.model.Film;
import java.util.List;

/**
 * ─────────────────────────────────────────────────────────────
 *  Interface Callback: Jembatan antara Controller dan View
 *
 *  Mengapa perlu interface ini?
 *  Request ke API berjalan di background thread (tidak bisa langsung
 *  update UI). Interface ini memungkinkan Controller memberitahu View
 *  "data sudah siap" atau "terjadi error" setelah operasi selesai.
 *
 *  Pola ini disebut "Callback Pattern" — sangat umum di Android.
 * ─────────────────────────────────────────────────────────────
 */
public interface FilmCallback {

    /**
     * Dipanggil saat GET berhasil mengambil daftar film dari API.
     * @param films List berisi semua objek Film yang sudah diparsing dari JSON
     */
    void onFilmsLoaded(List<Film> films);

    /**
     * Dipanggil saat POST berhasil menambahkan film baru ke server.
     * @param film Objek Film yang baru dibuat, sudah berisi ID dari server
     */
    void onFilmAdded(Film film);

    /**
     * Dipanggil saat terjadi error jaringan atau parsing.
     * @param message Pesan error yang akan ditampilkan ke user
     */
    void onError(String message);
}

