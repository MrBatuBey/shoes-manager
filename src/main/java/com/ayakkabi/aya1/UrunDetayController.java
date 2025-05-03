package com.ayakkabi.aya1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class UrunDetayController {
    @FXML
    private Label urunIdLabel;
    @FXML
    private Label modelAdiLabel;
    @FXML
    private Label renkLabel;
    @FXML
    private Label kategoriLabel;
    @FXML
    private Label fiyatLabel;
    @FXML
    private Label toplamStokLabel;

    @FXML
    private TableView<NumaraAdetRow> numaraAdetTable;
    @FXML
    private TableColumn<NumaraAdetRow, Integer> numaraColumn;
    @FXML
    private TableColumn<NumaraAdetRow, Integer> adetColumn;

    @FXML
    private TextField yeniNumaraField;
    @FXML
    private TextField yeniAdetField;
    @FXML
    private Button numaraEkleButton;

    @FXML
    private TextField fiyatGuncelleField;
    @FXML
    private Button fiyatGuncelleButton;

    private Urun urun;
    private ObservableList<NumaraAdetRow> numaraAdetList = FXCollections.observableArrayList();
    private Map<Integer, Integer> orjinalNumaraAdetMap = new HashMap<>();
    private double orjinalFiyat;

    public static class NumaraAdetRow {
        private final SimpleIntegerProperty numara;
        private final SimpleIntegerProperty adet;

        public NumaraAdetRow(int numara, int adet) {
            this.numara = new SimpleIntegerProperty(numara);
            this.adet = new SimpleIntegerProperty(adet);
        }

        public int getNumara() {
            return numara.get();
        }

        public SimpleIntegerProperty numaraProperty() {
            return numara;
        }

        public int getAdet() {
            return adet.get();
        }

        public void setAdet(int adet) {
            this.adet.set(adet);
        }

        public SimpleIntegerProperty adetProperty() {
            return adet;
        }
    }

    @FXML
    private void initialize() {
        // Tablo sütunlarını yapılandır
        numaraColumn.setCellValueFactory(cellData -> cellData.getValue().numaraProperty().asObject());
        adetColumn.setCellValueFactory(cellData -> cellData.getValue().adetProperty().asObject());

        // Adet sütunu için düzenlenebilir hücre fabrikası
        adetColumn.setCellFactory(column -> new TableCell<NumaraAdetRow, Integer>() {
            private final TextField textField = new TextField();

            {
                textField.setOnAction(e -> commitEdit(Integer.parseInt(textField.getText())));
                textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        try {
                            commitEdit(Integer.parseInt(textField.getText()));
                        } catch (NumberFormatException ex) {
                            cancelEdit();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (isEditing()) {
                    setText(null);
                    textField.setText(item.toString());
                    setGraphic(textField);
                } else {
                    setText(item.toString());
                    setGraphic(null);
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                setText(null);
                textField.setText(getItem().toString());
                setGraphic(textField);
                textField.requestFocus();
                textField.selectAll();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem().toString());
                setGraphic(null);
            }

            @Override
            public void commitEdit(Integer newValue) {
                super.commitEdit(newValue);
                NumaraAdetRow row = getTableView().getItems().get(getIndex());
                row.setAdet(newValue);
                guncelleToplamStok();
            }
        });

        // Numara-adet tablosuna sağ tıklama menüsü ekle
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Numarayı Sil");
        deleteItem.setOnAction(e -> {
            NumaraAdetRow selectedRow = numaraAdetTable.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Numara Sil");
                alert.setHeaderText("Numarayı Silme Onayı");
                alert.setContentText(selectedRow.getNumara() + " numarasını silmek istediğinizden emin misiniz?");

                if (alert.showAndWait().get() == ButtonType.OK) {
                    numaraAdetList.remove(selectedRow);
                    guncelleToplamStok();
                }
            }
        });
        contextMenu.getItems().add(deleteItem);
        numaraAdetTable.setContextMenu(contextMenu);

        numaraAdetTable.setItems(numaraAdetList);
    }

    public void setUrun(Urun urun) {
        this.urun = urun;

        // Ürün bilgilerini göster
        urunIdLabel.setText(String.valueOf(urun.getUrunId()));
        modelAdiLabel.setText(urun.getModelAdi());
        renkLabel.setText(urun.getRenk());
        kategoriLabel.setText(urun.getKategori());
        fiyatLabel.setText(String.format("%.2f TL", urun.getFiyat()));
        toplamStokLabel.setText(String.valueOf(urun.getStokMiktari()));

        // Orjinal değerleri sakla
        orjinalFiyat = urun.getFiyat();
        orjinalNumaraAdetMap = new HashMap<>(urun.getNumaraAdetMap());

        // Fiyat güncelleme alanına mevcut fiyatı ata
        fiyatGuncelleField.setText(String.format("%.2f", urun.getFiyat()));

        // Numara-adet tablosunu doldur
        numaraAdetList.clear();
        for (Map.Entry<Integer, Integer> entry : urun.getNumaraAdetMap().entrySet()) {
            numaraAdetList.add(new NumaraAdetRow(entry.getKey(), entry.getValue()));
        }
    }

    @FXML
    private void ekleNumaraAdet() {
        try {
            int numara = Integer.parseInt(yeniNumaraField.getText());
            int adet = Integer.parseInt(yeniAdetField.getText());

            // Numara aralığı kontrolü
            if (numara < 20 || numara > 50) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Numara 20-50 arasında olmalıdır.");
                return;
            }

            // Adet kontrolü
            if (adet <= 0) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Adet pozitif bir sayı olmalıdır.");
                return;
            }

            // Aynı numara var mı kontrol et
            for (NumaraAdetRow row : numaraAdetList) {
                if (row.getNumara() == numara) {
                    // Var olan numaranın adetini güncelle
                    row.setAdet(row.getAdet() + adet);
                    numaraAdetTable.refresh();
                    guncelleToplamStok();
                    yeniNumaraField.clear();
                    yeniAdetField.clear();
                    return;
                }
            }

            // Yeni numara ekle
            numaraAdetList.add(new NumaraAdetRow(numara, adet));
            guncelleToplamStok();

            // Alanları temizle
            yeniNumaraField.clear();
            yeniAdetField.clear();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir numara ve adet girin.");
        }
    }

    @FXML
    private void guncelleFiyat() {
        try {
            double yeniFiyat = Double.parseDouble(fiyatGuncelleField.getText());

            if (yeniFiyat <= 0) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Fiyat pozitif bir sayı olmalıdır.");
                return;
            }

            // Fiyatı güncelle
            fiyatLabel.setText(String.format("%.2f TL", yeniFiyat));

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir fiyat girin.");
        }
    }

    private void guncelleToplamStok() {
        int toplamStok = 0;
        for (NumaraAdetRow row : numaraAdetList) {
            toplamStok += row.getAdet();
        }
        toplamStokLabel.setText(String.valueOf(toplamStok));
    }

    @FXML
    private void kaydetDegisiklikler() {
        // Fiyatı güncelle
        try {
            double yeniFiyat = Double.parseDouble(fiyatGuncelleField.getText());
            urun.setFiyat(yeniFiyat);
        } catch (NumberFormatException e) {
            // Fiyat değişmediyse bir şey yapma
        }

        // Numara-adet bilgilerini güncelle
        urun.getNumaraAdetMap().clear();
        for (NumaraAdetRow row : numaraAdetList) {
            urun.addNumaraAdet(row.getNumara(), row.getAdet());
        }

        // Toplam stok miktarını güncelle
        int toplamStok = 0;
        for (NumaraAdetRow row : numaraAdetList) {
            toplamStok += row.getAdet();
        }
        urun.setStokMiktari(toplamStok);

        showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Değişiklikler kaydedildi.");
        kapat();
    }

    @FXML
    private void iptalEt() {
        // Değişiklikleri geri al
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("İptal");
        alert.setHeaderText("Değişiklikleri İptal Et");
        alert.setContentText("Tüm değişiklikler iptal edilecek. Devam etmek istiyor musunuz?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Orjinal değerlere geri dön
            urun.setFiyat(orjinalFiyat);
            urun.getNumaraAdetMap().clear();
            for (Map.Entry<Integer, Integer> entry : orjinalNumaraAdetMap.entrySet()) {
                urun.addNumaraAdet(entry.getKey(), entry.getValue());
            }
            kapat();
        }
    }

    @FXML
    private void kapat() {
        Stage stage = (Stage) modelAdiLabel.getScene().getWindow();
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