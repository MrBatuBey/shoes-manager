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

public class UrunEkleController {
    @FXML
    private TextField modelField;
    @FXML
    private TextField renkField;
    @FXML
    private ComboBox<String> kategoriCombo; // Yeni ComboBox
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
        kategoriCombo.getSelectionModel().selectFirst();

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
    }

    public void setUrunListesi(List<Urun> urunListesi) {
        this.urunListesi = urunListesi;
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
    private void ekleUrun() {
        String model = modelField.getText();
        String renk = renkField.getText().toLowerCase();
        String kategori = kategoriCombo.getValue(); // Kategori değerini al
        String fiyatStr = fiyatField.getText();
        String aciklama = aciklamaArea.getText();

        if (model.isEmpty() || renk.isEmpty() || kategori == null || fiyatStr.isEmpty() || numaraAdetList.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun ve en az bir numara ekleyin.");
            return;
        }

        try {
            double fiyat = Double.parseDouble(fiyatStr);
            int toplamStok = Integer.parseInt(toplamStokLabel.getText());
            int yeniId = urunListesi.isEmpty() ? 1 : urunListesi.get(urunListesi.size() - 1).getUrunId() + 1;

            // Kategori parametresini ekleyerek yeni Urun oluştur
            Urun yeniUrun = new Urun(yeniId, model, renk, kategori, fiyat, toplamStok);

            // Numara ve adetleri ekle
            for (NumaraAdet na : numaraAdetList) {
                yeniUrun.addNumaraAdet(Integer.parseInt(na.getNumara()), na.getAdet());
            }

            urunListesi.add(yeniUrun);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ürün başarıyla eklendi: " + model);

            // Formu kapat
            iptal();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir fiyat girin.");
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