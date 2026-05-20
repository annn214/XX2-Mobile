package com.f55124089.XX2.view;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.f55124089.XX2.DatabaseHelper;
import com.f55124089.XX2.R;
import com.f55124089.XX2.databinding.ActivityFavoriteBinding;
import com.f55124089.XX2.model.Film;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriteBinding binding;
    private DatabaseHelper dbHelper;
    private FilmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarFav);
        if (getSupportActionBar() != null) {
            // REVISI: Aktifkan panah kembali (Back)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("XX2");
        }

        // REVISI: Pasang listener klik pada panah toolbar
        binding.toolbarFav.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteFilms();
    }

    private void loadFavoriteFilms() {
        List<Film> favList = dbHelper.getAllFavorites();

        if (favList.isEmpty()) {
            binding.rvFavorites.setVisibility(View.GONE);
            binding.layoutEmptyFav.setVisibility(View.VISIBLE);
        } else {
            binding.rvFavorites.setVisibility(View.VISIBLE);
            binding.layoutEmptyFav.setVisibility(View.GONE);

            // Gunakan adapter grid (2 kolom) agar serasi dengan tampilan 'Semua Film'
            adapter = new FilmAdapter(this, favList, R.layout.item_film);
            binding.rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
            binding.rvFavorites.setAdapter(adapter);
        }
    }
}