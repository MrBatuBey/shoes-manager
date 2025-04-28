package com.ayakkabi.aya1;
import java.util.HashMap;
import java.util.Map;

public class Urun {
    private int urunId;
    private String modelAdi;
    private String renk;  // marka yerine renk
    private double fiyat;
    private int stokMiktari;
    private Map<Integer, Integer> numaraAdetMap; // Numara ve adet bilgisi

    public Urun(int urunId, String modelAdi, String renk, double fiyat, int stokMiktari) {  // marka yerine renk
        this.urunId = urunId;
        this.modelAdi = modelAdi;
        this.renk = renk;  // marka yerine renk
        this.fiyat = fiyat;
        this.stokMiktari = stokMiktari;
        this.numaraAdetMap = new HashMap<>();
    }

    public int getUrunId() {
        return urunId;
    }

    public String getModelAdi() {
        return modelAdi;
    }

    public String getRenk() {  // getMarka yerine getRenk
        return renk;
    }

    public double getFiyat() {
        return fiyat;
    }

    public int getStokMiktari() {
        return stokMiktari;
    }

    public Map<Integer, Integer> getNumaraAdetMap() {
        return numaraAdetMap;
    }

    public void setModelAdi(String modelAdi) {
        this.modelAdi = modelAdi;
    }

    public void setRenk(String renk) {  // setMarka yerine setRenk
        this.renk = renk;
    }

    public void setFiyat(double fiyat) {
        this.fiyat = fiyat;
    }

    public void setStokMiktari(int stokMiktari) {
        this.stokMiktari = stokMiktari;
    }

    public void addNumaraAdet(int numara, int adet) {
        numaraAdetMap.put(numara, adet);
    }

    @Override
    public String toString() {
        return "Ürün ID: " + urunId + ", Model: " + modelAdi + ", Renk: " + renk + ", Fiyat: " + fiyat + " TL, Stok: " + stokMiktari;
    }
}