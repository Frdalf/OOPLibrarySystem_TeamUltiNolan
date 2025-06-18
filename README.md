# OOPLibrarySystem_TeamUltiNolan
> Final Project – Pemrograman Berorientasi Objek (PBO)  
> 📌 Universitas Muhammadiyah Malang • 2025  

# Nama Anggota:
| Nama                       | NIM                | Peran                             |
|----------------------------|--------------------|------------------------------------|
| Syahrial Nur Faturrahman   | 202410370110009    | 🧠 Lead Developer, Testing & Docs |
| Naufal Muammar             | 202410370110027    | 🔧 Backend Specialist (Data Logic)|
| Farid Al Farizi            | 202410370110017    | 🎨 Frontend Specialist (JavaFX UI)|

## Pepustakaan kampus 📖
## Deskripsi 📜
OOPLibrarySystem_TeamUltiNolan adalah aplikasi desktop berbasis Java yang dirancang untuk membantu proses manajemen perpustakaan kampus secara digital. Sistem ini dibangun menggunakan teknologi Java dan JavaFX, serta menerapkan pola arsitektur Model View Controller (MVC) untuk menjaga pemisahan logika dan tampilan.
## Fungsionalitas Utama:
- 📚 Manajemen Data Buku dan Anggota (CRUD)
Tambah, ubah, hapus, dan cari data buku serta anggota perpustakaan.
- 📥 Peminjaman Buku
Anggota dapat meminjam buku. Transaksi dicatat otomatis ke dalam sistem.
- 📤 Pengembalian Buku & Perhitungan Denda
Sistem menghitung denda secara otomatis berdasarkan keterlambatan pengembalian.
- 🔎 Pencarian Buku
Pencarian interaktif berdasarkan ISBN, judul, atau nama pengarang.
- 🔐 Login dan Registrasi Pengguna
Sistem mendukung login sebagai admin maupun anggota, serta form registrasi pengguna baru.
- ⚠️ Validasi Input dan Notifikasi GUI
Menggunakan dialog JavaFX (Alert) untuk memberikan umpan balik saat terjadi kesalahan atau saat aksi berhasil dilakukan.
- 📄 Penyimpanan Data Menggunakan CSV
Seluruh data disimpan secara lokal dalam file .csv, dan dibaca kembali saat aplikasi dijalankan ulang.
- Project ini disusun untuk menyelesaikan **"Final Project Mata Kuliah Pemrograman Berorientasi Objek (PBO)"**
## 🧠 Arsitektur Sistem
Aplikasi ini menerapkan arsitektur Model–View–Controller (MVC) untuk memisahkan logika bisnis, antarmuka pengguna, dan kontrol alur aplikasi. Hal ini mempermudah pengembangan modular dan pemeliharaan kode.
- Model: Berisi logika dan struktur data, seperti entitas Book, Member, Transaction.
- View: Antarmuka grafis menggunakan JavaFX dan file .fxml.
- Controller: Menangani input dari pengguna dan menghubungkan View dengan Model.
## 🧩 Struktur Folder
- /src → Kode sumber (Java).
- /data → Data CSV.
- /docs → Dokumentasi teknis.
- /presentation → File presentasi.
## 🛠 Teknologi yang Digunakan
- 🧑‍💻 Java 11
- 🎨 JavaFX (GUI, Alert, TableView, SceneBuilder)
- 📦 Maven (Project Management)
- 📂 File I/O (BufferedReader / Writer) untuk CSV
- 🧪 JUnit / Manual Testing
- 💻 IntelliJ IDEA
## ⚙️ Cara Menjalankan Aplikasi
Aplikasi ini dapat dijalankan pada lingkungan Java 11+ dengan bantuan IntelliJ IDEA dan JavaFX SDK.
1. Pastikan Java JDK 11 dan JavaFX SDK sudah terpasang.
2. Clone repository:
   git clone https://github.com/Frdalf/OOPLibrarySystem_TeamUltiNolan.git
3. Buka folder Final project PBO di IntelliJ IDEA.
4. Jalankan file MainApp.java.
## 📄 Lisensi
Proyek ini dibuat sebagai bagian dari tugas akademik pada mata kuliah Pemrograman Berbasis Objek
Universitas Muhammadiyah Malang – 2025
📚 Digunakan untuk tujuan pembelajaran, tidak untuk dikomersialkan tanpa izin dari tim pengembang.
## 🙌 Ucapan Terima Kasih
Kami mengucapkan terima kasih kepada dosen pembimbing dan seluruh tim atas kerja sama dan semangat dalam menyelesaikan proyek ini. Proyek ini menjadi pengalaman berharga dalam kolaborasi, debugging, dan pengembangan aplikasi secara nyata.


