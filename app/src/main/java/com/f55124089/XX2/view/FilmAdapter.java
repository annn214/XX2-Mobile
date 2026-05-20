package com.f55124089.XX2.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.f55124089.XX2.R;
import com.f55124089.XX2.model.Film;

import java.util.ArrayList;
import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private final Context context;
    private List<Film> filmList;
    private final int layoutResId; // TAMBAHAN: Variabel untuk menyimpan ID layout

    // REVISI KONSTRUKTOR: Tambahkan parameter layoutResId
    public FilmAdapter(Context context, List<Film> filmList, int layoutResId) {
        this.context = context;
        this.filmList = filmList != null ? new ArrayList<>(filmList) : new ArrayList<>();
        this.layoutResId = layoutResId; // Simpan layout pilihan
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // REVISI: Gunakan layoutResId yang dikirim dari MainActivity
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        Film film = filmList.get(position);

        holder.tvJudul.setText(film.getJudul());
        holder.tvGenre.setText(film.getGenre());

        Glide.with(context)
                .load(film.getPoster())
                .placeholder(R.drawable.ic_poster_placeholder)
                .error(R.drawable.ic_poster_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("film_id", film.getId());
            intent.putExtra("film_judul", film.getJudul());
            intent.putExtra("film_genre", film.getGenre());
            intent.putExtra("film_poster", film.getPoster());
            intent.putExtra("film_deskripsi", film.getDeskripsi());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filmList != null ? filmList.size() : 0;
    }

    public void updateData(List<Film> newFilms) {
        this.filmList = new ArrayList<>(newFilms);
        notifyDataSetChanged();
    }

    public void addFilmAtTop(Film film) {
        if (this.filmList == null) {
            this.filmList = new ArrayList<>();
        }
        this.filmList.add(0, film);
        notifyItemInserted(0);
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvJudul;
        TextView tvGenre;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvJudul  = itemView.findViewById(R.id.tv_judul);
            tvGenre  = itemView.findViewById(R.id.tv_genre);
        }
    }
}