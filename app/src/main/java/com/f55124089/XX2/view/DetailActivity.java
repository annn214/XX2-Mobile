package com.f55124089.XX2.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.f55124089.XX2.DatabaseHelper;
import com.f55124089.XX2.R;
import com.f55124089.XX2.databinding.ActivityDetailBinding;
import com.f55124089.XX2.model.Film;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    // Variabel untuk Database Lokal
    private DatabaseHelper dbHelper;
    private boolean isFavorite = false;
    private Film currentFilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi Database
        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        displayFilmData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Film");
        }
    }

    private void displayFilmData() {
        String id       = getIntent().getStringExtra("film_id");
        String judul    = getIntent().getStringExtra("film_judul");
        String genre    = getIntent().getStringExtra("film_genre");
        String poster   = getIntent().getStringExtra("film_poster");
        String deskripsi = getIntent().getStringExtra("film_deskripsi");

        // Bungkus ulang data yang diterima menjadi objek Film
        currentFilm = new Film(judul, genre, poster, deskripsi);
        currentFilm.setId(id);

        // Cek ke Database: Apakah film ini sudah ada di daftar favorit?
        if (id != null) {
            isFavorite = dbHelper.isFavorite(id);
        }

        binding.tvJudul.setText(judul);
        binding.tvGenre.setText(genre);
        binding.tvDeskripsi.setText(deskripsi != null && !deskripsi.isEmpty() ? deskripsi : "Tidak ada deskripsi tersedia.");

        if (getSupportActionBar() != null && judul != null) {
            getSupportActionBar().setTitle(judul);
        }

        Glide.with(this)
                .load(poster)
                .placeholder(R.drawable.ic_poster_placeholder)
                .error(R.drawable.ic_poster_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .fitCenter()
                .into(binding.ivPosterDetail);
    }

    // MEMUNCULKAN MENU BINTANG DI POJOK KANAN ATAS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem favItem = menu.findItem(R.id.action_fav_toggle);

        // Ubah ikon bintang menyala/mati sesuai status di database
        if (isFavorite) {
            favItem.setIcon(android.R.drawable.btn_star_big_on); // Bintang Emas (Nyala)
        } else {
            favItem.setIcon(android.R.drawable.btn_star_big_off); // Bintang Abu (Mati)
        }
        return true;
    }

    // MENANGANI KLIK TOMBOL BACK & TOMBOL BINTANG
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.action_fav_toggle) {
            // Logika saat bintang diklik
            if (isFavorite) {
                // Jika sudah favorit, maka Hapus
                dbHelper.removeFavorite(currentFilm.getId());
                Toast.makeText(this, "Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
            } else {
                // Jika belum favorit, maka Simpan
                dbHelper.addFavorite(currentFilm);
                Toast.makeText(this, "Disimpan ke Favorit!", Toast.LENGTH_SHORT).show();
            }

            // Balikkan status dan refresh ikon bintang
            isFavorite = !isFavorite;
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}