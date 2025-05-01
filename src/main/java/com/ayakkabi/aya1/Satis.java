package com.ayakkabi.aya1;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Satis {
    private static int sonSatisId = 0;
    private int satisId;
    private int urunId;
    private String urunAdi;
    private String kategori; // Yeni kategori alanı
    private int numara;
    private int adet;
    private double birimFiyat;
    private double toplamFiyat;
    private LocalDateTime satisTarihi;
    // Opsiyonel müşteri bilgileri
    private String musteriAdi;
    private String telefon;
    private String not;

    public Satis(int urunId, String urunAdi, String kategori, int numara, int adet, double birimFiyat) {
        this.satisId = ++sonSatisId;
        this.urunId = urunId;
        this.urunAdi = urunAdi;
        this.kategori = kategori;
        this.numara = numara;
        this.adet = adet;
        this.birimFiyat = birimFiyat;
        this.toplamFiyat = birimFiyat * adet;
        this.satisTarihi = LocalDateTime.now();
    }

    public int getSatisId() {
        return satisId;
    }

    public int getUrunId() {
        return urunId;
    }

    public String getUrunAdi() {
        return urunAdi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getNumara() {
        return numara;
    }

    public int getAdet() {
        return adet;
    }

    public double getBirimFiyat() {
        return birimFiyat;
    }

    public double getToplamFiyat() {
        return toplamFiyat;
    }

    public LocalDateTime getSatisTarihi() {
        return satisTarihi;
    }

    public String getFormattedSatisTarihi() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return satisTarihi.format(formatter);
    }

    public String getMusteriAdi() {
        return musteriAdi;
    }

    public void setMusteriAdi(String musteriAdi) {
        this.musteriAdi = musteriAdi;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getNot() {
        return not;
    }

    public void setNot(String not) {
        this.not = not;
    }

    @Override
    public String toString() {
        return String.format("Satış ID: %d, Ürün: %s, Kategori: %s, Numara: %d, Adet: %d, Toplam: %.2f TL, Tarih: %s",
                satisId, urunAdi, kategori, numara, adet, toplamFiyat, getFormattedSatisTarihi());
    }
}