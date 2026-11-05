# Product: SimpleNote

## Vision
Aplikasi pencatatan pengeluaran yang terasa secepat menutup dompet.
Bukan aplikasi budgeting — murni jurnal keuangan digital dengan friction nol, dirancang untuk menumbuhkan kebiasaan mencatat secara alami tanpa paksaan atau rasa cemas.

## Primary Persona
- **User:** Diri sendiri (developer, 20–35 tahun, tech-savvy, iPhone user sehari-hari).
- **Konteks Penggunaan:** Langsung setelah transaksi terjadi di kasir, kafe, transportasi, atau belanja online kapanpun dan di manapun.
- **Job To Be Done:** "Saya ingin mencatat pengeluaran dalam waktu kurang dari 5 detik, dan sewaktu-waktu bisa melihat ke mana saja uang saya pergi bulan ini dengan visual yang tenang dan menyenangkan."

## Core Principles
1. **Zero Friction Entry:** Satu ketukan langsung ke form input, angka siap diketik tanpa navigasi berbelit.
2. **Calm & Non-Judgmental:** Tidak ada warna merah mencolok, alarm, atau penalti visual saat pengeluaran besar. Desain terasa seperti buku catatan/jurnal pribadi berkualitas tinggi.
3. **Data Privacy First:** 100% lokal di perangkat (offline-first, SwiftData), tanpa akun, tanpa cloud sync yang rumit di MVP.
4. **Immediate Insight:** Visualisasi instan berupa pembagian kategori dan grafik tren harian tanpa konfigurasi manual.

## KPI Sukses (Phase 1 - Personal Testing)
- ≥ 80% hari dalam sebulan memiliki minimal 1 catatan pengeluaran.
- Waktu input ≤ 5 detik per transaksi (buka app -> input nominal -> pilih kategori -> simpan).
- Zero crash dalam 30 hari pemakaian aktif.

## Non-Goals (MVP)
- Tidak ada fitur budgeting / batas anggaran ketat.
- Tidak ada cloud sync atau multi-device sync di MVP.
- Tidak ada integrasi perbankan otomatis / SMS scraper.
- Tidak ada widget atau push notification di tahap awal (fokus core in-app UX).
- Tidak ada multi-currency (fokus IDR dengan pemisah ribuan titik).
- Tidak ada versi Android di fase 1 (direncanakan untuk fase 2 setelah iOS terbukti).
