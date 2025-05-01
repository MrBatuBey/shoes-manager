package com.ayakkabi.aya1;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SatisYapController {
    @FXML
    private ComboBox<Urun> urunCombo;
    @FXML
    private Label modelLabel;
    @FXML
    private Label renkLabel;
    @FXML
    private Label kategoriLabel; // Yeni eklenen kategori etiketi
    @FXML
    private Label fiyatLabel;
    @FXML
    private ComboBox<Integer> numaraCombo;
    @FXML
    private Label stokLabel;
    @FXML
    private TextField adetField;
    @FXML
    private Label tutarLabel;
    @FXML
    private TextField musteriAdiField;
    @FXML
    private TextField telefonField;
    @FXML
    private TextArea notArea;
    private List<Urun> urunListesi;
    private List<Satis> satisListesi;

    public void setData(List<Urun> urunListesi, List<Satis> satisListesi) {
        this.urunListesi = urunListesi;
        this.satisListesi = satisListesi;
        // Ürün listesini ComboBox'a ekle
        urunCombo.setItems(FXCollections.observableArrayList(urunListesi));
        // Ürün seçildiğinde bilgileri güncelle
        urunCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateUrunBilgileri(newVal);
            }
        });
        // Numara seçildiğinde stok bilgisini güncelle
        numaraCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateStokBilgisi();
            }
        });
        // Adet değiştiğinde toplam tutarı güncelle
        adetField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateTutarBilgisi();
        });
        // İlk ürünü seç (eğer varsa)
        if (!urunListesi.isEmpty()) {
            urunCombo.getSelectionModel().selectFirst();
        }
    }

    private void updateUrunBilgileri(Urun urun) {
        modelLabel.setText("Model: " + urun.getModelAdi());
        renkLabel.setText("Renk: " + urun.getRenk());
        kategoriLabel.setText("Kategori: " + urun.getKategori()); // Kategori bilgisini göster
        fiyatLabel.setText(String.format("Fiyat: %.2f TL", urun.getFiyat()));
        // Numaraları güncelle
        List<Integer> numaralar = new ArrayList<>(urun.getNumaraAdetMap().keySet());
        numaralar.sort(Integer::compareTo); // Numaraları sırala
        numaraCombo.setItems(FXCollections.observableArrayList(numaralar));
        // İlk numarayı seç (eğer varsa)
        if (!numaralar.isEmpty()) {
            numaraCombo.getSelectionModel().selectFirst();
        } else {
            stokLabel.setText("0");
        }
    }

    private void updateStokBilgisi() {
        Urun secilenUrun = urunCombo.getValue();
        Integer secilenNumara = numaraCombo.getValue();
        if (secilenUrun != null && secilenNumara != null) {
            int stok = secilenUrun.getNumaraAdetMap().getOrDefault(secilenNumara, 0);
            stokLabel.setText(String.valueOf(stok));
        } else {
            stokLabel.setText("0");
        }
        updateTutarBilgisi();
    }

    private void updateTutarBilgisi() {
        Urun secilenUrun = urunCombo.getValue();
        if (secilenUrun != null) {
            try {
                int adet = adetField.getText().isEmpty() ? 0 : Integer.parseInt(adetField.getText());
                double birimFiyat = secilenUrun.getFiyat();
                double toplamTutar = adet * birimFiyat;
                tutarLabel.setText(String.format("%.2f TL", toplamTutar));
            } catch (NumberFormatException e) {
                tutarLabel.setText("0.00 TL");
            }
        } else {
            tutarLabel.setText("0.00 TL");
        }
    }

    @FXML
    private void satisiTamamla() {
        Urun secilenUrun = urunCombo.getValue();
        Integer secilenNumara = numaraCombo.getValue();
        if (secilenUrun == null || secilenNumara == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen ürün ve numara seçin.");
            return;
        }
        String adetStr = adetField.getText();
        if (adetStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen satış adedini girin.");
            return;
        }
        try {
            int adet = Integer.parseInt(adetStr);
            if (adet <= 0) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Satış adedi pozitif bir sayı olmalıdır.");
                return;
            }
            int mevcutStok = secilenUrun.getNumaraAdetMap().getOrDefault(secilenNumara, 0);
            if (adet > mevcutStok) {
                showAlert(Alert.AlertType.ERROR, "Hata",
                        String.format("Yeterli stok yok! %d numarada sadece %d adet mevcut.",
                                secilenNumara, mevcutStok));
                return;
            }
            // Stok güncelleme
            secilenUrun.getNumaraAdetMap().put(secilenNumara, mevcutStok - adet);
            secilenUrun.setStokMiktari(secilenUrun.getStokMiktari() - adet);
            // Satış kaydı oluşturma
            String musteriAdi = musteriAdiField.getText();
            String telefon = telefonField.getText();
            String not = notArea.getText();

            // Kategori bilgisini içeren yeni Satis nesnesi oluştur
            Satis yeniSatis = new Satis(
                    secilenUrun.getUrunId(),
                    secilenUrun.getModelAdi(),
                    secilenUrun.getKategori(), // Kategori bilgisi eklendi
                    secilenNumara,
                    adet,
                    secilenUrun.getFiyat()
            );

            // Müşteri bilgilerini ekle (eğer varsa)
            if (!musteriAdi.isEmpty()) {
                yeniSatis.setMusteriAdi(musteriAdi);
            }
            if (!telefon.isEmpty()) {
                yeniSatis.setTelefon(telefon);
            }
            if (!not.isEmpty()) {
                yeniSatis.setNot(not);
            }
            satisListesi.add(yeniSatis);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                    String.format("Satış başarıyla tamamlandı!\nÜrün: %s\nKategori: %s\nNumara: %d\nAdet: %d\nToplam: %.2f TL",
                            secilenUrun.getModelAdi(), secilenUrun.getKategori(), secilenNumara, adet, yeniSatis.getToplamFiyat()));
            // Pencereyi kapat
            iptal();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir adet girin.");
        }
    }

    @FXML
    private void iptal() {
        Stage stage = (Stage) urunCombo.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}