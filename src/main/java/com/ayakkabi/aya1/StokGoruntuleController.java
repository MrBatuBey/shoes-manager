package com.ayakkabi.aya1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StokGoruntuleController {
    @FXML
    private TextField aramaField;
    @FXML
    private ComboBox<String> kategoriFiltre;
    @FXML
    private TableView<Urun> urunTable;
    @FXML
    private TableColumn<Urun, Integer> idColumn;
    @FXML
    private TableColumn<Urun, String> modelColumn;
    @FXML
    private TableColumn<Urun, String> renkColumn;
    @FXML
    private TableColumn<Urun, String> kategoriColumn;
    @FXML
    private TableColumn<Urun, Double> fiyatColumn;
    @FXML
    private TableColumn<Urun, Integer> toplamStokColumn;
    @FXML
    private TableView<NumaraAdet> numaraAdetTable;
    @FXML
    private TableColumn<NumaraAdet, Integer> numaraColumn;
    @FXML
    private TableColumn<NumaraAdet, Integer> adetColumn;
    @FXML
    private Label toplamUrunLabel;
    @FXML
    private Button silButton; // Sil butonu

    private List<Urun> urunListesi;
    private ObservableList<Urun> observableUrunListesi;
    private FilteredList<Urun> filteredUrunListesi;

    public static class NumaraAdet {
        private final SimpleIntegerProperty numara;
        private final SimpleIntegerProperty adet;

        public NumaraAdet(int numara, int adet) {
            this.numara = new SimpleIntegerProperty(numara);
            this.adet = new SimpleIntegerProperty(adet);
        }

        public int getNumara() {
            return numara.get();
        }

        public int getAdet() {
            return adet.get();
        }
    }

    @FXML
    private void initialize() {
        // Kategori filtresini doldur
        kategoriFiltre.getItems().addAll("Tümü", "Erkek", "Kadın", "Çocuk");
        kategoriFiltre.getSelectionModel().selectFirst();

        // Kategori filtresi değişikliği dinleyicisi
        kategoriFiltre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filtreUygula();
        });

        // Ürün tablosu sütunlarını ayarla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("urunId"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("modelAdi"));
        renkColumn.setCellValueFactory(new PropertyValueFactory<>("renk"));
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        fiyatColumn.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        toplamStokColumn.setCellValueFactory(new PropertyValueFactory<>("stokMiktari"));

        // Fiyat sütunu için özel hücre fabrikası
        fiyatColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f TL", item));
                }
            }
        });

        // Numara-Adet tablosu sütunlarını ayarla
        numaraColumn.setCellValueFactory(new PropertyValueFactory<>("numara"));
        adetColumn.setCellValueFactory(new PropertyValueFactory<>("adet"));

        // Ürün seçildiğinde numara-adet detaylarını göster ve sil butonunu etkinleştir
        urunTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showNumaraAdetDetails(newSelection);
                silButton.setDisable(false); // Ürün seçildiğinde sil butonu etkinleştir
            } else {
                numaraAdetTable.getItems().clear();
                silButton.setDisable(true); // Ürün seçili değilse sil butonu devre dışı bırak
            }
        });

        // Çift tıklama ile ürün detay ekranını açma
        urunTable.setRowFactory(tv -> {
            TableRow<Urun> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Urun secilenUrun = row.getItem();
                    urunDetayGoster(secilenUrun);
                }
            });
            return row;
        });

        // Başlangıçta sil butonu devre dışı
        if (silButton != null) {
            silButton.setDisable(true);
        }
    }

    private void urunDetayGoster(Urun urun) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/urunDetay.fxml"));
            Parent root = loader.load();

            UrunDetayController controller = loader.getController();
            controller.setUrun(urun);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ürün Detayları - " + urun.getModelAdi());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Değişiklikleri tabloda güncelle
            urunTable.refresh();

            // Ürün numara-adet detaylarını güncelle
            showNumaraAdetDetails(urun);

            // Toplam ürün sayısını güncelle
            toplamUrunLabel.setText("Toplam Ürün: " + filteredUrunListesi.size());

            // Değişiklikleri kaydet
            kaydetDegisiklikler();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata",
                    "Ürün detayları gösterilirken bir hata oluştu: " + e.getMessage());
        }
    }

    // Ürün silme metodu
    @FXML
    private void urunSil() {
        Urun secilenUrun = urunTable.getSelectionModel().getSelectedItem();
        if (secilenUrun == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silmek için bir ürün seçin.");
            return;
        }

        // Silme işlemi onayını al
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ürün Sil");
        alert.setHeaderText("Ürün Silme Onayı");
        alert.setContentText("\"" + secilenUrun.getModelAdi() + "\" ürününü silmek istediğinizden emin misiniz?\n" +
                "Bu işlem geri alınamaz!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Ürünü listeden kaldır
            urunListesi.remove(secilenUrun);
            observableUrunListesi.remove(secilenUrun);

            // Tabloyu güncelle ve bilgi mesajı göster
            toplamUrunLabel.setText("Toplam Ürün: " + filteredUrunListesi.size());
            showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                    "\"" + secilenUrun.getModelAdi() + "\" ürünü başarıyla silindi.");

            // Değişiklikleri kaydet
            kaydetDegisiklikler();
        }
    }

    // Değişiklikleri kaydetmek için yardımcı metot
    private void kaydetDegisiklikler() {
        // MainController'a erişim yoksa burada doğrudan VeriDepolama'yı kullanabiliriz
        // Bu örnekte sadece ürünleri kaydediyoruz, ihtiyaca göre değiştirilebilir
        VeriDepolama.kaydet(urunListesi, new ArrayList<>(), new ArrayList<>());
    }

    public void setUrunListesi(List<Urun> urunListesi) {
        this.urunListesi = urunListesi;
        observableUrunListesi = FXCollections.observableArrayList(urunListesi);
        filteredUrunListesi = new FilteredList<>(observableUrunListesi, p -> true);
        urunTable.setItems(filteredUrunListesi);
        toplamUrunLabel.setText("Toplam Ürün: " + urunListesi.size());
        // İlk ürünü seç (eğer varsa)
        if (!urunListesi.isEmpty()) {
            urunTable.getSelectionModel().selectFirst();
        }
    }

    private void showNumaraAdetDetails(Urun urun) {
        ObservableList<NumaraAdet> numaraAdetList = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : urun.getNumaraAdetMap().entrySet()) {
            numaraAdetList.add(new NumaraAdet(entry.getKey(), entry.getValue()));
        }
        numaraAdetTable.setItems(numaraAdetList);
    }

    @FXML
    private void aramaYap() {
        filtreUygula();
    }

    private void filtreUygula() {
        String aramaKelimesi = aramaField.getText().toLowerCase();
        String kategoriSecimi = kategoriFiltre.getValue();

        filteredUrunListesi.setPredicate(urun -> {
            // Kategori filtresi
            boolean kategoriUygun = true;
            if (!"Tümü".equals(kategoriSecimi)) {
                kategoriUygun = urun.getKategori().equals(kategoriSecimi);
            }

            // Arama filtresi
            boolean aramaUygun = true;
            if (aramaKelimesi != null && !aramaKelimesi.isEmpty()) {
                String modelAdi = urun.getModelAdi().toLowerCase();
                String renk = urun.getRenk().toLowerCase();
                String urunId = String.valueOf(urun.getUrunId());

                aramaUygun = modelAdi.contains(aramaKelimesi) ||
                        renk.contains(aramaKelimesi) ||
                        urunId.contains(aramaKelimesi);
            }

            return kategoriUygun && aramaUygun;
        });

        toplamUrunLabel.setText("Toplam Ürün: " + filteredUrunListesi.size());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void kapat() {
        Stage stage = (Stage) aramaField.getScene().getWindow();
        stage.close();
    }
}