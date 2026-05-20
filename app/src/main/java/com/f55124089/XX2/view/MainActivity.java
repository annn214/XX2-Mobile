package com.f55124089.XX2.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

// TAMBAHAN IMPORT GLIDE
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import com.f55124089.XX2.R;
import com.f55124089.XX2.controller.FilmCallback;
import com.f55124089.XX2.controller.FilmController;
import com.f55124089.XX2.databinding.ActivityMainBinding;
import com.f55124089.XX2.model.Film;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilmCallback {

    private ActivityMainBinding binding;
    private FilmController controller;

    private FilmAdapter trendingAdapter;
    private FilmAdapter allFilmsAdapter;

    private List<Film> masterFilmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new FilmController();
        masterFilmList = new ArrayList<>();

        // Inisialisasi adapter dengan layout berbeda
        trendingAdapter = new FilmAdapter(this, new ArrayList<>(), R.layout.item_film_horizontal);
        allFilmsAdapter = new FilmAdapter(this, new ArrayList<>(), R.layout.item_film);

        setupUI();
        loadFilms();
    }

    private void setupUI() {
        // Mengaktifkan toolbar agar bisa dipasang ikon menu (Favorite)
        setSupportActionBar(binding.toolbar);

        // Setup RecyclerView Trending (Horizontal)
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvTrending.setLayoutManager(horizontalLayout);
        binding.rvTrending.setAdapter(trendingAdapter);
        binding.rvTrending.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        // Setup RecyclerView Semua Film (Grid)
        GridLayoutManager gridLayout = new GridLayoutManager(this, 2);
        binding.rvFilms.setLayoutManager(gridLayout);
        binding.rvFilms.setAdapter(allFilmsAdapter);
        binding.rvFilms.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        binding.fabAddFilm.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFilmActivity.class);
            startActivityForResult(intent, 100);
        });

        binding.swipeRefresh.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light
        );
        binding.swipeRefresh.setOnRefreshListener(this::loadFilms);

        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFilm(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterFilm(String text) {
        if (text.isEmpty()) {
            binding.tvTitleTrending.setVisibility(View.VISIBLE);
            binding.rvTrending.setVisibility(View.VISIBLE);
            if (binding.cvBannerPromo != null) {
                binding.cvBannerPromo.setVisibility(View.VISIBLE);
            }
            binding.tvTitleAll.setText("🎬 Semua Film");
            splitAndDisplayData(masterFilmList);
        } else {
            binding.tvTitleTrending.setVisibility(View.GONE);
            binding.rvTrending.setVisibility(View.GONE);
            if (binding.cvBannerPromo != null) {
                binding.cvBannerPromo.setVisibility(View.GONE);
            }
            binding.tvTitleAll.setText("🔍 Hasil Pencarian");

            List<Film> filteredList = new ArrayList<>();
            for (Film item : masterFilmList) {
                if (item.getJudul().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            allFilmsAdapter.updateData(filteredList);
        }
    }

    private void loadFilms() {
        showShimmer();
        controller.getAllFilms(this);
    }

    @Override
    public void onFilmsLoaded(List<Film> films) {
        hideShimmer();
        binding.swipeRefresh.setRefreshing(false);

        if (films.isEmpty()) {
            showEmptyState();
        } else {
            this.masterFilmList.clear();
            this.masterFilmList.addAll(films);
            splitAndDisplayData(films);
            showContent();
        }
    }

    private void splitAndDisplayData(List<Film> films) {
        if (!films.isEmpty() && binding.cvBannerPromo != null) {
            Film premiereFilm = films.get(0);

            Glide.with(this)
                    .load(premiereFilm.getPoster())
                    .placeholder(R.drawable.ic_poster_placeholder)
                    .error(R.drawable.ic_poster_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .centerCrop()
                    .into(binding.ivBannerImage);
        }
        // =========================================================

        List<Film> trendingList = new ArrayList<>();
        List<Film> allFilmsList = new ArrayList<>();

        if (films.size() <= 3) {
            allFilmsList.addAll(films);
            binding.tvTitleTrending.setVisibility(View.GONE);
            binding.rvTrending.setVisibility(View.GONE);
        } else {
            binding.tvTitleTrending.setVisibility(View.VISIBLE);
            binding.rvTrending.setVisibility(View.VISIBLE);

            int maxTrending = Math.min(films.size(), 5);
            for (int i = 0; i < maxTrending; i++) {
                trendingList.add(films.get(i));
            }
            for (int i = maxTrending; i < films.size(); i++) {
                allFilmsList.add(films.get(i));
            }
        }

        trendingAdapter.updateData(trendingList);
        allFilmsAdapter.updateData(allFilmsList);
    }

    @Override
    public void onFilmAdded(Film film) {
        masterFilmList.add(0, film);
        splitAndDisplayData(masterFilmList);
        showContent();
        binding.rvTrending.scrollToPosition(0);
        Toast.makeText(this, "Film \"" + film.getJudul() + "\" berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        hideShimmer();
        binding.swipeRefresh.setRefreshing(false);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        if (masterFilmList.isEmpty()) {
            showEmptyState();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Film newFilm = new Film(
                    data.getStringExtra("judul"),
                    data.getStringExtra("genre"),
                    data.getStringExtra("poster"),
                    data.getStringExtra("deskripsi")
            );
            newFilm.setId(data.getStringExtra("id"));
            controller.addFilm(newFilm, this);
        }
    }

    private void showShimmer() {
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.GONE);
    }

    private void hideShimmer() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
    }

    private void showContent() {
        binding.layoutContent.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.shutdown();
    }
}