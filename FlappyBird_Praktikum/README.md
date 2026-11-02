# Flappy Bird - IntelliJ IDEA

Project Java Swing sederhana, tanpa library eksternal (folder `lib/` dengan MySQL connector tidak dipakai).

## Cara menjalankan di IntelliJ IDEA
1. Extract ZIP.
2. **File > Open**, pilih folder `TP 6` (atau `FlappyBird_Praktikum`).
3. Jika diminta/ada tulisan *Project SDK is not defined*, pilih **JDK 17 atau lebih baru**
   (**File > Project Structure > Project > SDK**).
4. Buka `src/App.java`, klik segitiga hijau di samping `public static void main`, lalu **Run 'App.main()'**.

Jika IntelliJ belum mengenali `src` sebagai source:
- Klik kanan folder `src` > **Mark Directory as > Sources Root**.

## Menjalankan lewat terminal
```
cd FlappyBird_Praktikum
javac -d out src/*.java
cp -r src/assets out/assets
java -cp out App
```

## Kontrol
- `SPACE`: mulai permainan / membuat burung mengepak ke atas (satu tekanan = satu kepakan).
- Menahan SPACE tidak membuat burung terus naik; tekan berulang untuk menjaga keseimbangan.
- Tidak menekan apa pun: burung jatuh karena gravitasi.
- Lewati celah pipa untuk menambah skor.
- Menabrak pipa, tanah, atau langit-langit: Game Over.
- `SPACE` lagi (setelah jeda singkat): mulai ulang.

## Struktur
- `src/App.java`: entry point dan window.
- `src/Player.java`: data burung dan velocity.
- `src/Logic.java`: game loop, gravitasi, obstacle, collision, skor, restart.
- `src/View.java`: tampilan game.
- `src/assets/bird.png`: aset burung.
