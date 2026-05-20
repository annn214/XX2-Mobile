package com.f55124089.XX2.model;

/**
 * ─────────────────────────────────────────────────────────────
 *  LAYER: MODEL
 *  Tanggung jawab: Merepresentasikan struktur data satu objek Film.
 *  Kelas ini TIDAK boleh berisi logika bisnis atau kode UI.
 *  Setiap field harus cocok persis dengan key JSON dari API MockAPI.
 * ─────────────────────────────────────────────────────────────
 */
public class Film {

    // Field 'id' diberikan otomatis oleh server MockAPI saat POST berhasil
    private String id;

    // Field 'judul' harus sama persis dengan key JSON di API: "judul"
    private String judul;

    // Genre film: "Action", "Drama", "Sci-Fi", dll.
    private String genre;

    // URL lengkap gambar poster (digunakan Glide untuk mengunduh & menampilkan)
    private String poster;

    // Deskripsi/sinopsis film
    private String deskripsi;

    // ── Constructor kosong wajib ada untuk proses JSON parsing ──
    // Saat kita parse JSON ke objek Film, Java butuh constructor tanpa parameter
    public Film() {}

    // ── Constructor berparameter untuk membuat objek Film dari kode Java ──
    // Dipakai di AddFilmActivity saat user mengisi form tambah film
    public Film(String judul, String genre, String poster, String deskripsi) {
        this.judul = judul;
        this.genre = genre;
        this.poster = poster;
        this.deskripsi = deskripsi;
    }

    // ── Getter & Setter ──────────────────────────────────────────────────
    // Getter dipakai oleh Adapter untuk menampilkan data di RecyclerView
    // Setter dipakai oleh Controller saat parsing JSON dari API

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

}