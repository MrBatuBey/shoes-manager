package com.ayakkabi.aya1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Not {
    private static int sonNotId = 0;
    private int notId;
    private String musteriAdi;
    private String telefon;
    private String notTipi; // "Kapora" veya "Tamir" veya "Diğer"
    private String notIcerigi;
    private double tutar; // Kapora tutarı veya tamir ücreti
    private boolean tamamlandi;
    private LocalDateTime olusturmaTarihi;
    private LocalDateTime teslimTarihi; // Tahmini teslim tarihi

    public Not(String musteriAdi, String telefon, String notTipi, String notIcerigi, double tutar, LocalDateTime teslimTarihi) {
        this.notId = ++sonNotId;
        this.musteriAdi = musteriAdi;
        this.telefon = telefon;
        this.notTipi = notTipi;
        this.notIcerigi = notIcerigi;
        this.tutar = tutar;
        this.tamamlandi = false;
        this.olusturmaTarihi = LocalDateTime.now();
        this.teslimTarihi = teslimTarihi;
    }

    // Getter ve Setter metotları
    public int getNotId() {
        return notId;
    }

    public void setNotId(int notId) {
        this.notId = notId;
        // sonNotId'yi güncelle
        if (notId > sonNotId) {
            sonNotId = notId;
        }
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

    public String getNotTipi() {
        return notTipi;
    }

    public void setNotTipi(String notTipi) {
        this.notTipi = notTipi;
    }

    public String getNotIcerigi() {
        return notIcerigi;
    }

    public void setNotIcerigi(String notIcerigi) {
        this.notIcerigi = notIcerigi;
    }

    public double getTutar() {
        return tutar;
    }

    public void setTutar(double tutar) {
        this.tutar = tutar;
    }

    public boolean isTamamlandi() {
        return tamamlandi;
    }

    public void setTamamlandi(boolean tamamlandi) {
        this.tamamlandi = tamamlandi;
    }

    public LocalDateTime getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public LocalDateTime getTeslimTarihi() {
        return teslimTarihi;
    }

    public void setTeslimTarihi(LocalDateTime teslimTarihi) {
        this.teslimTarihi = teslimTarihi;
    }

    public String getFormattedOlusturmaTarihi() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return olusturmaTarihi.format(formatter);
    }

    public String getFormattedTeslimTarihi() {
        if (teslimTarihi == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return teslimTarihi.format(formatter);
    }

    public String getDurumText() {
        return tamamlandi ? "Tamamlandı" : "Bekliyor";
    }

    @Override
    public String toString() {
        return String.format("Not ID: %d, Müşteri: %s, Tip: %s, Durum: %s, Tarih: %s",
                notId, musteriAdi, notTipi, getDurumText(), getFormattedOlusturmaTarihi());
    }
}