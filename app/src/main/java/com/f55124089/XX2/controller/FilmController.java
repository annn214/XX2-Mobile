package com.f55124089.XX2.controller;

import com.f55124089.XX2.FilmApiService;
import com.f55124089.XX2.model.Film;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

/**
 * ─────────────────────────────────────────────────────────────
 * LAYER: CONTROLLER (VERSI RETROFIT)
 * Tanggung jawab: Mengelola request jaringan ke MockAPI
 * menggunakan library Retrofit (Type-Safe HTTP Client).
 * ─────────────────────────────────────────────────────────────
 * Kelompok: Ann
 */
public class FilmController {

    private final FilmApiService apiService;

    public FilmController() {
        // Konfigurasi dasar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://68ff8dfbe02b16d1753e765d.mockapi.io/") // URL utama API tanpa kata "film"
                .addConverterFactory(GsonConverterFactory.create()) // Mengubah teks JSON ke Objek Java secara otomatis
                .build();

        // Inisialisasi apiService menggunakan konfigurasi Retrofit
        apiService = retrofit.create(FilmApiService.class);
    }

    /**
     * Mengambil daftar film dari server
     */
    public void getAllFilms(FilmCallback callback) {
        // .enqueue() otomatis menjalankan request di background thread (Asynchronous)
        apiService.getAllFilms().enqueue(new Callback<List<Film>>() {
            @Override
            public void onResponse(Call<List<Film>> call, Response<List<Film>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Retrofit otomatis kembali ke Main Thread saat mengembalikan data ke View
                    callback.onFilmsLoaded(response.body());
                } else {
                    callback.onError("Gagal mengambil data: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Film>> call, Throwable t) {
                // Terjadi error koneksi jaringan terputus atau URL tidak ditemukan
                callback.onError("Gangguan jaringan: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Menambahkan film baru ke server
     */
    public void addFilm(Film film, FilmCallback callback) {
        // .enqueue() otomatis mengirim data objek Film sebagai JSON Body
        apiService.addFilm(film).enqueue(new Callback<Film>() {
            @Override
            public void onResponse(Call<Film> call, Response<Film> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onFilmAdded(response.body());
                } else {
                    callback.onError("Gagal menyimpan film: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Film> call, Throwable t) {
                callback.onError("Gagal mengirim data: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Karena Retrofit mengelola daur hidup thread-nya sendiri,
     * kita tidak memerlukan shutdown ExecutorService manual lagi.
     */
    public void shutdown() {
        // Dibiarkan kosong untuk menjaga kecocokan fungsi di MainActivity
    }
}