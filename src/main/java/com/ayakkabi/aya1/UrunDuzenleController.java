package com.ayakkabi.aya1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class UrunDuzenleController {
    @FXML
    private ComboBox<Urun> urunSecim;
    @FXML
    private TextField modelField;
    @FXML
    private TextField renkField;
    @FXML
    private ComboBox<String> kategoriCombo; // Yeni eklenen ComboBox
    @FXML
    private TextField fiyatField;
    @FXML
    private TextField numaraField;
    @FXML
    private TextField adetField;
    @FXML
    private TableView<NumaraAdet> numaraTable;
    @FXML
    private TableColumn<NumaraAdet, String> numaraColumn;
    @FXML
    private TableColumn<NumaraAdet, Integer> adetColumn;
    @FXML
    private Label toplamStokLabel;
    @FXML
    private TextArea aciklamaArea;
    private List<Urun> urunListesi;
    private ObservableList<NumaraAdet> numaraAdetList = FXCollections.observableArrayList();

    public static class NumaraAdet {
        private final SimpleStringProperty numara;
        private final SimpleIntegerProperty adet;
        public NumaraAdet(String numara, int adet) {
            this.numara = new SimpleStringProperty(numara);
            this.adet = new SimpleIntegerProperty(adet);
        }
        public String getNumara() {
            return numara.get();
        }
        public int getAdet() {
            return adet.get();
        }
        public void setAdet(int adet) {
            this.adet.set(adet);
        }
    }

    @FXML
    private void initialize() {
        numaraColumn.setCellValueFactory(new PropertyValueFactory<>("numara"));
        adetColumn.setCellValueFactory(new PropertyValueFactory<>("adet"));
        numaraTable.setItems(numaraAdetList);

        // Kategori ComboBox'ını doldur
        kategoriCombo.getItems().addAll("Erkek", "Kadın", "Çocuk");

        // Sağ tıklama menüsü ekle
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Sil");
        deleteItem.setOnAction(event -> {
            NumaraAdet selected = numaraTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                numaraAdetList.remove(selected);
                updateToplamStok();
            }
        });
        contextMenu.getItems().add(deleteItem);
        numaraTable.setContextMenu(contextMenu);

        // Ürün seçildiğinde bilgileri doldur
        urunSecim.getSelectionModel().selectedItemProperty().addListener((obs, oldUrun, newUrun) -> {
            if (newUrun != null) {
                modelField.setText(newUrun.getModelAdi());
                renkField.setText(newUrun.getRenk());
                kategoriCombo.setValue(newUrun.getKategori()); // Kategori değerini seç
                fiyatField.setText(String.valueOf(newUrun.getFiyat()));

                // Numara ve adetleri tabloya ekle
                numaraAdetList.clear();
                for (Map.Entry<Integer, Integer> entry : newUrun.getNumaraAdetMap().entrySet()) {
                    numaraAdetList.add(new NumaraAdet(String.valueOf(entry.getKey()), entry.getValue()));
                }
                updateToplamStok();
            }
        });
    }

    public void setUrunListesi(List<Urun> urunListesi) {
        this.urunListesi = urunListesi;
        urunSecim.setItems(FXCollections.observableArrayList(urunListesi));
    }

    @FXML
    private void numaraEkle() {
        String numara = numaraField.getText();
        String adetStr = adetField.getText();
        if (numara.isEmpty() || adetStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen numara ve adet girin.");
            return;
        }
        try {
            int numaraInt = Integer.parseInt(numara);
            int adet = Integer.parseInt(adetStr);
            // Numara aralığını 20-50 olarak değiştir
            if (numaraInt < 20 || numaraInt > 50) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Numara 20-50 arasında olmalıdır.");
                return;
            }
            if (adet <= 0) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Adet pozitif bir sayı olmalıdır.");
                return;
            }
            // Aynı numara daha önce eklenmişse, adeti güncelle
            boolean numaraVarMi = false;
            for (NumaraAdet na : numaraAdetList) {
                if (na.getNumara().equals(numara)) {
                    na.setAdet(na.getAdet() + adet);
                    numaraVarMi = true;
                    break;
                }
            }
            // Yeni numara ise listeye ekle
            if (!numaraVarMi) {
                numaraAdetList.add(new NumaraAdet(numara, adet));
            }
            // Alanları temizle
            numaraField.clear();
            adetField.clear();
            // Toplam stoku güncelle
            updateToplamStok();
            // Tabloyu yenile
            numaraTable.refresh();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir numara ve adet girin.");
        }
    }

    private void updateToplamStok() {
        int toplamStok = 0;
        for (NumaraAdet na : numaraAdetList) {
            toplamStok += na.getAdet();
        }
        toplamStokLabel.setText(String.valueOf(toplamStok));
    }

    @FXML
    private void kaydetDegisiklikler() {
        Urun secilenUrun = urunSecim.getValue();
        if (secilenUrun != null) {
            try {
                String model = modelField.getText();
                String renk = renkField.getText();
                String kategori = kategoriCombo.getValue(); // Kategori değerini al
                double fiyat = Double.parseDouble(fiyatField.getText());
                int toplamStok = Integer.parseInt(toplamStokLabel.getText());

                if (model.isEmpty() || renk.isEmpty() || kategori == null || numaraAdetList.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun ve en az bir numara ekleyin.");
                    return;
                }

                secilenUrun.setModelAdi(model);
                secilenUrun.setRenk(renk);
                secilenUrun.setKategori(kategori); // Kategoriyi ayarla
                secilenUrun.setFiyat(fiyat);
                secilenUrun.setStokMiktari(toplamStok);

                // Numara ve adetleri güncelle
                secilenUrun.getNumaraAdetMap().clear();
                for (NumaraAdet na : numaraAdetList) {
                    secilenUrun.addNumaraAdet(Integer.parseInt(na.getNumara()), na.getAdet());
                }

                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Değişiklikler kaydedildi!");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir sayı girin.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen bir ürün seçin.");
        }
    }

    @FXML
    private void iptal() {
        Stage stage = (Stage) modelField.getScene().getWindow();
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