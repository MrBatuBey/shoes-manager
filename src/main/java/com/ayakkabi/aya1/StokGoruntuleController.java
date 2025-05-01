package com.ayakkabi.aya1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class StokGoruntuleController {
    @FXML
    private TextField aramaField;
    @FXML
    private ComboBox<String> kategoriFiltre; // Yeni eklenen kategori filtresi
    @FXML
    private TableView<Urun> urunTable;
    @FXML
    private TableColumn<Urun, Integer> idColumn;
    @FXML
    private TableColumn<Urun, String> modelColumn;
    @FXML
    private TableColumn<Urun, String> renkColumn;
    @FXML
    private TableColumn<Urun, String> kategoriColumn; // Yeni eklenen kategori sütunu
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
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori")); // Kategori sütunu
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

        // Ürün seçildiğinde numara-adet detaylarını göster
        urunTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showNumaraAdetDetails(newSelection);
            } else {
                numaraAdetTable.getItems().clear();
            }
        });
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

    @FXML
    private void kapat() {
        Stage stage = (Stage) aramaField.getScene().getWindow();
        stage.close();
    }
}