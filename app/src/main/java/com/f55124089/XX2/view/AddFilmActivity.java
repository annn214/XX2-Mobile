package com.f55124089.XX2.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.f55124089.XX2.controller.FilmCallback;
import com.f55124089.XX2.controller.FilmController;
import com.f55124089.XX2.databinding.ActivityAddFilmBinding;
import com.f55124089.XX2.model.Film;

import java.util.List;

public class AddFilmActivity extends AppCompatActivity implements FilmCallback {

    private ActivityAddFilmBinding binding;
    private FilmController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan baris di bawah ini tidak error. Jika error, lakukan Build > Rebuild Project
        binding = ActivityAddFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new FilmController();

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarAdd);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("XX2");
        }
    }

    private void setupClickListeners() {
        // Tombol panah kembali custom di samping judul halaman
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.btnSimpan.setOnClickListener(v -> {
            if (validateInput()) {
                submitFilm();
            }
        });
    }

    private boolean validateInput() {
        String judul    = binding.etJudul.getText().toString().trim();
        String genre    = binding.etGenre.getText().toString().trim();
        String poster   = binding.etPoster.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();

        // REVISI: Karena tidak pakai TextInputLayout, kita set error langsung di EditText
        if (TextUtils.isEmpty(judul)) {
            binding.etJudul.setError("Judul film tidak boleh kosong");
            binding.etJudul.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(genre)) {
            binding.etGenre.setError("Genre tidak boleh kosong");
            binding.etGenre.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(poster)) {
            binding.etPoster.setError("URL poster tidak boleh kosong");
            binding.etPoster.requestFocus();
            return false;
        }

        if (!poster.startsWith("http://") && !poster.startsWith("https://")) {
            binding.etPoster.setError("Masukkan URL yang valid (dimulai dengan http:// atau https://)");
            binding.etPoster.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(deskripsi)) {
            binding.etDeskripsi.setError("Deskripsi tidak boleh kosong");
            binding.etDeskripsi.requestFocus();
            return false;
        }

        return true;
    }

    private void submitFilm() {
        String judul    = binding.etJudul.getText().toString().trim();
        String genre    = binding.etGenre.getText().toString().trim();
        String poster   = binding.etPoster.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();

        Film film = new Film(judul, genre, poster, deskripsi);
        setLoadingState(true);
        controller.addFilm(film, this);
    }

    @Override
    public void onFilmsLoaded(List<Film> films) {
        // GET films tidak dipakai di sini
    }

    @Override
    public void onFilmAdded(Film film) {
        setLoadingState(false);
        Toast.makeText(this, "Film berhasil ditambahkan!", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", film.getId());
        resultIntent.putExtra("judul", film.getJudul());
        resultIntent.putExtra("genre", film.getGenre());
        resultIntent.putExtra("poster", film.getPoster());
        resultIntent.putExtra("deskripsi", film.getDeskripsi());
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    @Override
    public void onError(String message) {
        setLoadingState(false);
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }

    private void setLoadingState(boolean isLoading) {
        binding.btnSimpan.setEnabled(!isLoading);
        binding.btnSimpan.setText(isLoading ? "Menyimpan..." : "Simpan Film");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.shutdown();
    }
}