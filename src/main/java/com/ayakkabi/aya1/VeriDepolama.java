package com.ayakkabi.aya1;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VeriDepolama {
    private static final String URUNLER_DOSYA = "urunler.csv";
    private static final String NUMARA_STOK_DOSYA = "numara_stok.csv";
    private static final String SATISLAR_DOSYA = "satislar.csv";
    private static final String NOTLAR_DOSYA = "notlar.csv";
    private static final String KULLANICILAR_DOSYA = "kullanicilar.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void kaydet(List<Urun> urunListesi, List<Satis> satisListesi, List<Not> notListesi) {
        kaydetUrunler(urunListesi);
        kaydetNumaraStok(urunListesi);
        kaydetSatislar(satisListesi);
        kaydetNotlar(notListesi);
    }

    // Eski kaydet metodu (geriye dönük uyumluluk için)
    public static void kaydet(List<Urun> urunListesi, List<Satis> satisListesi) {
        kaydetUrunler(urunListesi);
        kaydetNumaraStok(urunListesi);
        kaydetSatislar(satisListesi);
    }

    private static void kaydetUrunler(List<Urun> urunListesi) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(URUNLER_DOSYA))) {
            // Başlık satırı
            writer.println("urun_id,model_adi,renk,kategori,fiyat,stok_miktari");
            // Veri satırları
            for (Urun urun : urunListesi) {
                writer.println(
                        urun.getUrunId() + "," +
                                escapeCSV(urun.getModelAdi()) + "," +
                                escapeCSV(urun.getRenk()) + "," +
                                escapeCSV(urun.getKategori()) + "," +
                                urun.getFiyat() + "," +
                                urun.getStokMiktari()
                );
            }
            System.out.println("Ürünler başarıyla kaydedildi.");
        } catch (IOException e) {
            System.err.println("Ürünler kaydedilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void kaydetNumaraStok(List<Urun> urunListesi) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NUMARA_STOK_DOSYA))) {
            // Başlık satırı
            writer.println("urun_id,numara,adet");
            // Veri satırları
            for (Urun urun : urunListesi) {
                for (Map.Entry<Integer, Integer> entry : urun.getNumaraAdetMap().entrySet()) {
                    writer.println(
                            urun.getUrunId() + "," +
                                    entry.getKey() + "," +
                                    entry.getValue()
                    );
                }
            }
            System.out.println("Numara-stok bilgileri başarıyla kaydedildi.");
        } catch (IOException e) {
            System.err.println("Numara-stok bilgileri kaydedilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void kaydetSatislar(List<Satis> satisListesi) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SATISLAR_DOSYA))) {
            // Başlık satırı - kategori eklendi
            writer.println("satis_id,urun_id,urun_adi,kategori,numara,adet,birim_fiyat,toplam_fiyat,satis_tarihi,musteri_adi,telefon,not");
            // Veri satırları
            for (Satis satis : satisListesi) {
                writer.println(
                        satis.getSatisId() + "," +
                                satis.getUrunId() + "," +
                                escapeCSV(satis.getUrunAdi()) + "," +
                                escapeCSV(satis.getKategori()) + "," +
                                satis.getNumara() + "," +
                                satis.getAdet() + "," +
                                satis.getBirimFiyat() + "," +
                                satis.getToplamFiyat() + "," +
                                satis.getSatisTarihi().format(DATE_FORMATTER) + "," +
                                escapeCSV(satis.getMusteriAdi()) + "," +
                                escapeCSV(satis.getTelefon()) + "," +
                                escapeCSV(satis.getNot())
                );
            }
            System.out.println("Satışlar başarıyla kaydedildi.");
        } catch (IOException e) {
            System.err.println("Satışlar kaydedilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void kaydetNotlar(List<Not> notListesi) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOTLAR_DOSYA))) {
            // Başlık satırı
            writer.println("not_id,musteri_adi,telefon,not_tipi,not_icerigi,tutar,tamamlandi,olusturma_tarihi,teslim_tarihi");
            // Veri satırları
            for (Not not : notListesi) {
                writer.println(
                        not.getNotId() + "," +
                                escapeCSV(not.getMusteriAdi()) + "," +
                                escapeCSV(not.getTelefon()) + "," +
                                escapeCSV(not.getNotTipi()) + "," +
                                escapeCSV(not.getNotIcerigi()) + "," +
                                not.getTutar() + "," +
                                not.isTamamlandi() + "," +
                                not.getOlusturmaTarihi().format(DATE_FORMATTER) + "," +
                                (not.getTeslimTarihi() != null ? not.getTeslimTarihi().format(DATE_FORMATTER) : "")
                );
            }
            System.out.println("Notlar başarıyla kaydedildi.");
        } catch (IOException e) {
            System.err.println("Notlar kaydedilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void kaydetKullanici(String ad, String soyad, String email, String sifre) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(KULLANICILAR_DOSYA, true))) {
            // Dosya boşsa başlık satırını ekle
            if (new File(KULLANICILAR_DOSYA).length() == 0) {
                writer.println("ad,soyad,email,sifre");
            }
            
            // Yeni kullanıcıyı ekle
            writer.println(
                escapeCSV(ad) + "," +
                escapeCSV(soyad) + "," +
                escapeCSV(email) + "," +
                escapeCSV(sifre)
            );
            
            System.out.println("Kullanıcı başarıyla kaydedildi.");
        } catch (IOException e) {
            System.err.println("Kullanıcı kaydedilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean kullaniciVarMi(String email) {
        File kullanicilarFile = new File(KULLANICILAR_DOSYA);
        if (!kullanicilarFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(KULLANICILAR_DOSYA))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = parseCSVLine(line);
                if (parts.length >= 3 && parts[2].equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Kullanıcı kontrolü yapılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean kullaniciDogrula(String email, String sifre) {
        File kullanicilarFile = new File(KULLANICILAR_DOSYA);
        if (!kullanicilarFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(KULLANICILAR_DOSYA))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = parseCSVLine(line);
                if (parts.length >= 4 && parts[2].equals(email) && parts[3].equals(sifre)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Kullanıcı doğrulama yapılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Sayısal değerleri güvenli bir şekilde parse etmek için yardımcı metot
    private static int safeParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Ondalıklı sayı ise, önce double'a çevirip sonra int'e dönüştür
            try {
                return (int) Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                System.err.println("Geçersiz sayı formatı: " + value);
                return 0; // Varsayılan değer
            }
        }
    }

    public static List<Urun> yukleUrunler() {
        List<Urun> urunListesi = new ArrayList<>();
        Map<Integer, Urun> urunMap = new HashMap<>();
        // Ürünleri yükle
        File urunlerFile = new File(URUNLER_DOSYA);
        if (urunlerFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(URUNLER_DOSYA))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue; // Başlık satırını atla
                    }
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 6) { // Kategori eklendiğinden eleman sayısı arttı
                        int id = safeParseInt(parts[0]);
                        String modelAdi = parts[1];
                        String renk = parts[2];
                        String kategori = parts[3];
                        double fiyat = Double.parseDouble(parts[4]);
                        int stokMiktari = safeParseInt(parts[5]);
                        Urun urun = new Urun(id, modelAdi, renk, kategori, fiyat, stokMiktari);
                        urunListesi.add(urun);
                        urunMap.put(id, urun);
                    }
                }
                System.out.println("Ürünler başarıyla yüklendi.");
            } catch (IOException e) {
                System.err.println("Ürünler yüklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Ürünler yüklenirken beklenmeyen hata: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Ürünler dosyası bulunamadı. Yeni bir liste oluşturuluyor.");
        }
        // Numara-stok bilgilerini yükle
        File numaraStokFile = new File(NUMARA_STOK_DOSYA);
        if (numaraStokFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(NUMARA_STOK_DOSYA))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue; // Başlık satırını atla
                    }
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 3) {
                        int urunId = safeParseInt(parts[0]);
                        int numara = safeParseInt(parts[1]);
                        int adet = safeParseInt(parts[2]);
                        Urun urun = urunMap.get(urunId);
                        if (urun != null) {
                            urun.addNumaraAdet(numara, adet);
                        }
                    }
                }
                System.out.println("Numara-stok bilgileri başarıyla yüklendi.");
            } catch (IOException e) {
                System.err.println("Numara-stok bilgileri yüklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Numara-stok bilgileri yüklenirken beklenmeyen hata: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Numara-stok dosyası bulunamadı.");
        }
        return urunListesi;
    }

    public static List<Satis> yukleSatislar() {
        List<Satis> satisListesi = new ArrayList<>();
        File satislarFile = new File(SATISLAR_DOSYA);
        if (satislarFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(SATISLAR_DOSYA))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue; // Başlık satırını atla
                    }
                    try {
                        String[] parts = parseCSVLine(line);
                        if (parts.length >= 8) { // En az 8 alan olmalı
                            int satisId = safeParseInt(parts[0]);
                            int urunId = safeParseInt(parts[1]);
                            String urunAdi = parts[2];

                            // Kategori için güvenli kontrol
                            String kategori = parts.length > 3 ? parts[3] : "Belirtilmemiş";

                            int numara = safeParseInt(parts.length > 4 ? parts[4] : "0");
                            int adet = safeParseInt(parts.length > 5 ? parts[5] : "0");

                            // Fiyat değerleri için güvenli parse
                            double birimFiyat = 0.0;
                            if (parts.length > 6 && !parts[6].isEmpty()) {
                                try {
                                    birimFiyat = Double.parseDouble(parts[6]);
                                } catch (NumberFormatException e) {
                                    System.err.println("Geçersiz birim fiyat formatı: " + parts[6]);
                                }
                            }

                            // Kategori içeren Satis nesnesi oluştur
                            Satis satis = new Satis(urunId, urunAdi, kategori, numara, adet, birimFiyat);

                            // Müşteri bilgileri
                            if (parts.length > 9 && !parts[9].isEmpty()) {
                                satis.setMusteriAdi(parts[9]);
                            }
                            if (parts.length > 10 && !parts[10].isEmpty()) {
                                satis.setTelefon(parts[10]);
                            }
                            if (parts.length > 11 && !parts[11].isEmpty()) {
                                satis.setNot(parts[11]);
                            }
                            satisListesi.add(satis);
                        }
                    } catch (Exception e) {
                        System.err.println("Satır işlenirken hata oluştu: " + line);
                        System.err.println("Hata detayı: " + e.getMessage());
                        e.printStackTrace();
                        // Hatayı yutup diğer satırları işlemeye devam et
                    }
                }
                System.out.println("Satışlar başarıyla yüklendi.");
            } catch (IOException e) {
                System.err.println("Satışlar yüklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Satışlar yüklenirken beklenmeyen hata: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Satışlar dosyası bulunamadı. Yeni bir liste oluşturuluyor.");
        }
        return satisListesi;
    }

    public static List<Not> yukleNotlar() {
        List<Not> notListesi = new ArrayList<>();
        File notlarFile = new File(NOTLAR_DOSYA);
        if (notlarFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(NOTLAR_DOSYA))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue; // Başlık satırını atla
                    }
                    try {
                        String[] parts = parseCSVLine(line);
                        if (parts.length >= 8) {
                            int notId = safeParseInt(parts[0]);
                            String musteriAdi = parts[1];
                            String telefon = parts[2];
                            String notTipi = parts[3];
                            String notIcerigi = parts[4];

                            // Tutar için güvenli parse
                            double tutar = 0.0;
                            if (parts.length > 5 && !parts[5].isEmpty()) {
                                try {
                                    tutar = Double.parseDouble(parts[5]);
                                } catch (NumberFormatException e) {
                                    System.err.println("Geçersiz tutar formatı: " + parts[5]);
                                }
                            }

                            boolean tamamlandi = parts.length > 6 && "true".equalsIgnoreCase(parts[6]);

                            // Tarih alanları için güvenli parse
                            LocalDateTime olusturmaTarihi = LocalDateTime.now();
                            if (parts.length > 7 && !parts[7].isEmpty()) {
                                try {
                                    olusturmaTarihi = LocalDateTime.parse(parts[7], DATE_FORMATTER);
                                } catch (Exception e) {
                                    System.err.println("Geçersiz oluşturma tarihi formatı: " + parts[7]);
                                }
                            }

                            LocalDateTime teslimTarihi = null;
                            if (parts.length > 8 && !parts[8].isEmpty()) {
                                try {
                                    teslimTarihi = LocalDateTime.parse(parts[8], DATE_FORMATTER);
                                } catch (Exception e) {
                                    System.err.println("Geçersiz teslim tarihi formatı: " + parts[8]);
                                }
                            }

                            Not not = new Not(musteriAdi, telefon, notTipi, notIcerigi, tutar, teslimTarihi);
                            not.setNotId(notId);
                            not.setTamamlandi(tamamlandi);
                            not.setOlusturmaTarihi(olusturmaTarihi);
                            notListesi.add(not);
                        }
                    } catch (Exception e) {
                        System.err.println("Not satırı işlenirken hata oluştu: " + line);
                        System.err.println("Hata detayı: " + e.getMessage());
                        // Hatayı yutup diğer satırları işlemeye devam et
                    }
                }
                System.out.println("Notlar başarıyla yüklendi.");
            } catch (IOException e) {
                System.err.println("Notlar yüklenirken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Notlar yüklenirken beklenmeyen hata: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Notlar dosyası bulunamadı. Yeni bir liste oluşturuluyor.");
        }
        return notListesi;
    }

    // CSV formatında virgül ve tırnak işaretlerini düzgün işlemek için yardımcı metotlar
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // Virgül, çift tırnak veya yeni satır içeriyorsa, çift tırnak içine al
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Çift tırnakları iki çift tırnak yaparak kaçır
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Kaçırılmış çift tırnak
                    field.append('"');
                    i++; // Bir sonraki karakteri atla
                } else {
                    // Tırnak durumunu değiştir
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Alan sonu
                result.add(field.toString());
                field.setLength(0); // StringBuilder'ı temizle
            } else {
                field.append(c);
            }
        }
        // Son alanı ekle
        result.add(field.toString());
        return result.toArray(new String[0]);
    }
}