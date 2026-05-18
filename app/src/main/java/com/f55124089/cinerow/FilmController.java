package com.f55124089.cinerow;

import android.os.Handler;
import android.os.Looper;

import com.f55124089.cinerow.Film;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ─────────────────────────────────────────────────────────────
 *  LAYER: CONTROLLER
 *  Tanggung jawab:
 *   1. Menerima perintah dari View (Activity)
 *   2. Menjalankan logika bisnis (request ke API, parsing JSON)
 *   3. Mengembalikan hasil ke View melalui Callback
 *
 *  Controller TIDAK boleh mengetahui soal TextView, RecyclerView, dsb.
 *  Controller TIDAK boleh dijalankan langsung di UI Thread.
 * ─────────────────────────────────────────────────────────────
 */
public class FilmController {

    // URL dasar API MockAPI — semua endpoint berawal dari sini
    private static final String BASE_URL = "https://68ff8dfbe02b16d1753e765d.mockapi.io/film";

    // OkHttpClient adalah client HTTP — dibuat sekali, dipakai berkali-kali (lebih efisien)
    private final OkHttpClient client;

    // ExecutorService: pool thread untuk menjalankan network request di background
    // Menggunakan newSingleThreadExecutor karena request dilakukan satu per satu
    private final ExecutorService executor;

    // Handler untuk mengirim hasil dari background thread KEMBALI ke UI thread
    // Semua update UI (setText, notify RecyclerView) HARUS di UI thread
    private final Handler mainHandler;

    public FilmController() {
        this.client = new OkHttpClient();
        // Thread pool dengan 2 thread: cukup untuk GET + POST secara bersamaan
        this.executor = Executors.newFixedThreadPool(2);
        // Looper.getMainLooper() = antrian pesan di UI thread utama
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ════════════════════════════════════════════════════════════════
    //  GET — Ambil semua film dari server
    // ════════════════════════════════════════════════════════════════

    /**
     * Mengambil daftar film dari API secara asynchronous.
     * Setelah selesai, memanggil callback.onFilmsLoaded() atau callback.onError()
     * di UI thread sehingga View bisa langsung update tampilan.
     *
     * @param callback Interface yang menghubungkan hasil ke View
     */
    public void getAllFilms(FilmCallback callback) {
        // submit() melemparkan pekerjaan ke background thread — tidak memblokir UI
        executor.submit(() -> {
            // Membuat objek Request untuk HTTP GET ke BASE_URL
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()               // method GET
                    .build();

            try {
                // client.newCall(request).execute() → mengirim request dan menunggu response
                // Ini berjalan di background thread, jadi aman (tidak ANR)
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    // HTTP code bukan 2xx — kirim error ke View lewat UI thread
                    postError(callback, "Server error: " + response.code());
                    return;
                }

                // response.body().string() → membaca seluruh isi response sebagai String
                String responseBody = response.body().string();

                // Parse JSON array menjadi List<Film>
                List<Film> films = parseFilmsFromJson(responseBody);

                // Setelah berhasil, kembali ke UI thread untuk update View
                mainHandler.post(() -> callback.onFilmsLoaded(films));

            } catch (IOException e) {
                // IOException terjadi saat masalah jaringan (timeout, no internet, dll)
                postError(callback, "Gagal terhubung: " + e.getMessage());
            } catch (Exception e) {
                // Exception umum untuk error parsing JSON
                postError(callback, "Error parsing data: " + e.getMessage());
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  POST — Kirim film baru ke server
    // ════════════════════════════════════════════════════════════════

    /**
     * Mengirim data film baru ke server menggunakan HTTP POST.
     * Request body berformat JSON sesuai struktur yang diharapkan MockAPI.
     *
     * @param film     Objek Film yang akan dikirim (tanpa ID, server yang assign)
     * @param callback Interface untuk memberitahu View hasil operasi
     */
    public void addFilm(Film film, FilmCallback callback) {
        executor.submit(() -> {
            // MediaType.parse() mendefinisikan format data yang dikirim: JSON
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            // film.toJson() mengubah objek Film menjadi string JSON
            // RequestBody.create() membungkus string JSON sebagai body request
            RequestBody body = RequestBody.create(film.toJson(), JSON);

            // Membangun request POST dengan URL, method, dan body
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)           // method POST dengan body JSON
                    .addHeader("Content-Type", "application/json") // header wajib untuk POST JSON
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    postError(callback, "Gagal menambahkan film: " + response.code());
                    return;
                }

                String responseBody = response.body().string();

                // Server mengembalikan objek film yang baru dibuat (sudah ada ID-nya)
                Film addedFilm = parseFilmFromJson(new JSONObject(responseBody));

                // Kembali ke UI thread: beritahu View bahwa film berhasil ditambahkan
                mainHandler.post(() -> callback.onFilmAdded(addedFilm));

            } catch (IOException e) {
                postError(callback, "Gagal terhubung: " + e.getMessage());
            } catch (Exception e) {
                postError(callback, "Error: " + e.getMessage());
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  Helper: Parsing JSON
    // ════════════════════════════════════════════════════════════════

    /**
     * Mengubah string JSON array menjadi List<Film>.
     * Contoh input: [{"id":"1","judul":"Dune",...}, {"id":"2",...}]
     */
    private List<Film> parseFilmsFromJson(String jsonString) throws Exception {
        List<Film> films = new ArrayList<>();

        // JSONArray: membaca string JSON yang berisi array (diawali "[")
        JSONArray jsonArray = new JSONArray(jsonString);

        // Iterasi setiap elemen array, ubah menjadi objek Film
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            films.add(parseFilmFromJson(jsonObject));
        }
        return films;
    }

    /**
     * Mengubah satu JSONObject menjadi satu objek Film.
     * optString() dipakai agar tidak crash jika key tidak ada (lebih aman dari getString())
     */
    private Film parseFilmFromJson(JSONObject obj) throws Exception {
        Film film = new Film();
        // optString(key, defaultValue) — tidak throw exception jika key tidak ada
        film.setId(obj.optString("id", ""));
        film.setJudul(obj.optString("judul", ""));
        film.setGenre(obj.optString("genre", ""));
        film.setPoster(obj.optString("poster", ""));
        film.setDeskripsi(obj.optString("deskripsi", ""));
        return film;
    }

    // ════════════════════════════════════════════════════════════════
    //  Helper: Posting error ke UI thread
    // ════════════════════════════════════════════════════════════════

    /**
     * Helper method agar tidak perlu menulis mainHandler.post() berulang kali.
     * Selalu kirim error message ke UI thread sebelum memanggil onError().
     */
    private void postError(FilmCallback callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }

    /**
     * Dipanggil saat Activity di-destroy untuk membersihkan thread yang masih berjalan.
     * Mencegah memory leak karena thread terus berjalan meski Activity sudah mati.
     */
    public void shutdown() {
        executor.shutdown();
    }
}

