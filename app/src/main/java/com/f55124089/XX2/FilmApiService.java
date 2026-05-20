package com.f55124089.XX2;

import com.f55124089.XX2.model.Film;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Interface untuk mendefinisikan rute / endpoint HTTP.
 * Retrofit akan otomatis membuat implementasi dari rute-rute ini.
 */
public interface FilmApiService {

    // Mengambil semua daftar film dari endpoint "film" dengan metode GET
    @GET("film")
    Call<List<Film>> getAllFilms();

    // Menambahkan film baru ke endpoint "film" dengan metode POST
    @POST("film")
    Call<Film> addFilm(@Body Film film);
}