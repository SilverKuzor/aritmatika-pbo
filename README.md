# Kalkulator CRUD

Proyek Spring Boot, Thymeleaf, dan PostgreSQL dengan form dan tabel di satu halaman, mengikuti susunan contoh CRUD Mahasiswa.

## Menjalankan di Windows

1. Pastikan JDK 21 atau lebih baru dan PostgreSQL sudah terpasang. Ekstrak ZIP lalu buka folder KalkulatorCRUD yang berisi pom.xml.
2. Jika database PBO_TRPL belum ada, jalankan `CREATE DATABASE "PBO_TRPL";` di PostgreSQL. Database dari proyek contoh juga bisa dipakai.
3. Jalankan di PowerShell. Ganti kata sandinya dengan kata sandi PostgreSQL milikmu.

   ```powershell
   $env:DB_PASSWORD = "kata_sandi_postgres"
   .\mvnw.cmd spring-boot:run
   ```

   Jika akun PostgreSQL bukan postgres, set juga DB_USER. Jika alamat atau nama database berbeda, set DB_URL dengan URL JDBC yang sesuai.

4. Buka http://localhost:8080. Hibernate membuat tabel operasi otomatis.

Tombol = menyimpan dua bilangan dan operator. Kedua input tetap tampil setelah dihitung. Hasil dihitung saat ditampilkan dan tidak menjadi kolom database. Edit mengisi kembali form dari baris yang dipilih. Tekan = untuk menyimpan perubahan pada baris itu. Tombol Hapus menghapus baris dari database.
