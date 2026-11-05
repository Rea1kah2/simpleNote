# Requirements Specification: SimpleNote Core Tracker

## 1. Glosarium Istilah
- **SimpleNote:** Aplikasi iOS pencatatan pengeluaran pribadi dengan fokus friction nol.
- **Expense (Pengeluaran):** Catatan transaksi keuangan keluar yang terdiri dari nominal uang, kategori, catatan opsional, dan timestamp.
- **Category (Kategori):** Pengelompokan pengeluaran (misal: Makan & Minum, Transportasi) dengan ikon emoji dan nama.
- **Zero Friction:** Pengalaman pengguna di mana proses pencatatan dari membuka lembar input hingga selesai memakan waktu kurang dari 5 detik dengan jumlah ketukan seminimal mungkin.
- **Calm Interface:** Estetika antarmuka yang tenang, minim noise visual, menggunakan palet sage green, tanpa elemen kecemasan finansial (anxiety-free).

## 2. User Stories
- **US-1 (Pencatatan Cepat):** Sebagai pengguna yang baru saja membayar sesuatu, saya ingin dapat langsung memasukkan nominal dan memilih kategori dengan satu ketukan, agar saya tidak merasa terbebani untuk mencatat setiap pengeluaran.
- **US-2 (Ringkasan Hari Ini):** Sebagai pengguna, saya ingin melihat ringkasan total uang yang sudah saya belanjakan hari ini di layar utama, agar saya tetap sadar akan ritme pengeluaran harian saya.
- **US-3 (Riwayat Kalender):** Sebagai pengguna, saya ingin melihat riwayat pengeluaran yang terorganisir per hari dan kalender bulanan dengan tanda hari aktif, agar saya mudah meninjau aktivitas pengeluaran di hari-hari sebelumnya.
- **US-4 (Visualisasi Pengeluaran):** Sebagai pengguna, saya ingin melihat proporsi kategori pengeluaran dan tren belanja harian dalam bentuk grafik yang sederhana dan elegan, agar saya dapat mengevaluasi kebiasaan saya setiap akhir bulan.
- **US-5 (Kategori Kustom):** Sebagai pengguna, saya ingin dapat menambah kategori baru yang sesuai dengan kebutuhan hidup saya, agar pengelompokan terasa relevan dan personal.

## 3. Kriteria Penerimaan Berbasis EARS

### 3.1. Pencatatan Pengeluaran (Core Entry)
- **WHEN** pengguna menekan tombol tambah (`+`) pada layar Today, **THE** SimpleNote **SHALL** menampilkan lembar input (`AddExpenseSheet`) dengan papan angka siap menerima input tanpa delay.
- **WHEN** pengguna mengetik angka pada papan angka, **THE** SimpleNote **SHALL** memformat nominal secara realtime ke format Rupiah (`Rp X.XXX`).
- **WHEN** pengguna memilih salah satu chip kategori, **THE** SimpleNote **SHALL** memberikan highlight visual (warna sage green lembut) pada kategori yang dipilih.
- **IF** nominal pengeluaran adalah 0 atau kosong, **THEN THE** SimpleNote **SHALL** menonaktifkan tombol simpan dan mencegah transaksi disimpan.
- **WHEN** tombol simpan ditekan pada form valid, **THE** SimpleNote **SHALL** menyimpan transaksi ke penyimpanan persisten SwiftData, menutup sheet, dan memperbarui daftar hari ini secara otomatis.

### 3.2. Layar Today (Daily Overview)
- **THE** SimpleNote **SHALL** menghitung dan menampilkan total akumulasi pengeluaran hari ini secara real-time.
- **WHEN** daftar transaksi hari ini kosong, **THE** SimpleNote **SHALL** menampilkan pesan sambutan yang menenangkan tanpa nada menuntut.
- **WHEN** pengguna mengusap (*swipe-to-delete*) item transaksi pada daftar, **THE** SimpleNote **SHALL** menghapus catatan tersebut dari database dan mengupdate total hari ini.

### 3.3. Layar History (Timeline & Kalender)
- **THE** SimpleNote **SHALL** menampilkan kalender bulanan yang menandai setiap tanggal yang memiliki catatan transaksi dengan titik aksen.
- **WHEN** pengguna memilih tanggal tertentu pada kalender, **THE** SimpleNote **SHALL** memfokuskan daftar transaksi pada tanggal yang dipilih.
- **WHEN** pengguna beralih bulan sebelumnya atau berikutnya, **THE** SimpleNote **SHALL** memuat riwayat transaksi untuk bulan yang dipilih.

### 3.4. Layar Stats (Analitik & Visualisasi)
- **THE** SimpleNote **SHALL** menghitung persentase kontribusi setiap kategori terhadap total pengeluaran bulan berjalan.
- **THE** SimpleNote **SHALL** menampilkan grafik donat kategori menggunakan Swift Charts dengan warna bertema tenang.
- **THE** SimpleNote **SHALL** menampilkan grafik batang harian yang memperlihatkan distribusi nominal belanja sepanjang tanggal dalam bulan tersebut.
- **IF** tidak ada transaksi pada bulan yang dipilih, **THEN THE** SimpleNote **SHALL** menyajikan tampilan kosong yang ramah dengan panduan pencatatan.

### 3.5. Pengaturan & Kategori (Settings)
- **THE** SimpleNote **SHALL** menyediakan 10 kategori bawaan saat aplikasi pertama kali dijalankan.
- **WHEN** pengguna membuat kategori baru dengan nama dan emoji, **THE** SimpleNote **SHALL** menyimpan kategori tersebut dan menampilkannya di urutan pemilihan input.
- **IF** kategori dihapus oleh pengguna, **THEN THE** SimpleNote **SHALL** menjaga transaksi terkait tetap utuh dengan memindahkan relasi kategori ke status tidak berkategori (`nil`).

### 3.6. Edit Transaksi (Sprint 2)
- **WHEN** pengguna mengetuk (*tap*) salah satu baris transaksi di layar Today atau History, **THE** SimpleNote **SHALL** membuka lembar edit (`EditExpenseSheet`) dengan data transaksi yang sudah terisi lengkap (nominal, kategori, catatan, tanggal).
- **WHEN** pengguna mengubah field dan menekan tombol simpan perubahan, **THE** SimpleNote **SHALL** memperbarui catatan di penyimpanan persisten JSON tanpa membuat catatan duplikat baru, lalu menutup sheet dan merefleksikan perubahan seketika.

### 3.7. Pencarian & Filter Riwayat (Sprint 2)
- **WHEN** pengguna mengetikkan kata kunci pada kolom pencarian di layar History, **THE** SimpleNote **SHALL** menyaring daftar transaksi secara realtime berdasarkan kecocokan teks catatan atau nama kategori.
- **WHEN** pengguna memilih filter chip kategori, **THE** SimpleNote **SHALL** hanya menampilkan transaksi dengan kategori yang bersangkutan.

### 3.8. Pengingat Cerdas Harian (Sprint 2)
- **WHEN** opsi pengingat diaktifkan di tab Pengaturan, **THE** SimpleNote **SHALL** meminta izin notifikasi lokal dan menjadwalkan notifikasi harian pada jam yang ditentukan pengguna (default: 21:00).
- **IF** pengguna sudah mencatat setidaknya 1 transaksi pada hari tersebut, **THEN THE** SimpleNote **SHALL** secara cerdas membatalkan pengingat hari itu agar tidak mengganggu pengguna (*non-intrusive*).

### 3.9. Pintasan Cepat Home Screen (Sprint 2 Quick Action)
- **WHEN** pengguna melakukan long-press pada ikon aplikasi SimpleNote di Home Screen dan memilih "Catat Pengeluaran", **THE** SimpleNote **SHALL** langsung membuka lembar input transaksi baru seketika setelah aplikasi aktif.

### 3.10. Aksen Visual & Hirarki Tipografi (Sprint 2 Craftsmanship)
- **THE** SimpleNote **SHALL** mengadopsi palet Oranye Terracotta (`#C1440E` / `#F97316`) sebagai warna aksen utama berkarakter hangat dan tegas tanpa klise AI.
- **THE** SimpleNote **SHALL** menampilkan angka total pengeluaran hari ini dengan ukuran 52pt Black Rounded sebagai elemen dominan utama layar Today.

## 4. Non-Functional Requirements
- **Performance:** App launch to interactive time < 400ms.
- **Input Speed:** Total alur pencatatan selesai dalam ≤ 5 detik.
- **Zero Network Requirement:** Aplikasi berfungsi 100% offline tanpa membutuhkan koneksi internet.
- **Accessibility:** Mendukung Dynamic Type dan kontras warna yang memenuhi standar WCAG AA.
