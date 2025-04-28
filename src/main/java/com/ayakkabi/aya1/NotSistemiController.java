package com.ayakkabi.aya1;

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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class NotSistemiController {
    @FXML
    private TextField aramaField;
    @FXML
    private ComboBox<String> durumFiltre;
    @FXML
    private ComboBox<String> tipFiltre;
    @FXML
    private TableView<Not> notTable;
    @FXML
    private TableColumn<Not, Integer> idColumn;
    @FXML
    private TableColumn<Not, String> musteriColumn;
    @FXML
    private TableColumn<Not, String> telefonColumn;
    @FXML
    private TableColumn<Not, String> tipColumn;
    @FXML
    private TableColumn<Not, String> durumColumn;
    @FXML
    private TableColumn<Not, String> tarihColumn;
    @FXML
    private TableColumn<Not, String> teslimTarihiColumn;
    @FXML
    private TableColumn<Not, Double> tutarColumn;
    @FXML
    private TextArea notIcerikArea;
    @FXML
    private Label toplamNotLabel;

    private List<Not> notListesi;
    private ObservableList<Not> observableNotListesi;
    private FilteredList<Not> filteredNotListesi;

    @FXML
    private void initialize() {
        // Tablo sütunlarını ayarla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("notId"));
        musteriColumn.setCellValueFactory(new PropertyValueFactory<>("musteriAdi"));
        telefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        tipColumn.setCellValueFactory(new PropertyValueFactory<>("notTipi"));
        durumColumn.setCellValueFactory(new PropertyValueFactory<>("durumText"));
        tarihColumn.setCellValueFactory(new PropertyValueFactory<>("formattedOlusturmaTarihi"));
        teslimTarihiColumn.setCellValueFactory(new PropertyValueFactory<>("formattedTeslimTarihi"));
        tutarColumn.setCellValueFactory(new PropertyValueFactory<>("tutar"));

        // Tutar sütunu için özel hücre fabrikası
        tutarColumn.setCellFactory(column -> new TableCell<>() {
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

        // Not seçildiğinde içeriğini göster
        notTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                notIcerikArea.setText(newSelection.getNotIcerigi());
            } else {
                notIcerikArea.clear();
            }
        });

        // Filtre combobox'larını ayarla
        durumFiltre.getItems().addAll("Tümü", "Bekleyenler", "Tamamlananlar");
        durumFiltre.getSelectionModel().selectFirst();

        tipFiltre.getItems().addAll("Tümü", "Kapora", "Tamir", "Diğer");
        tipFiltre.getSelectionModel().selectFirst();

        // Filtre değişikliklerini dinle
        durumFiltre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filtreUygula());
        tipFiltre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filtreUygula());
    }

    public void setNotListesi(List<Not> notListesi) {
        this.notListesi = notListesi;
        observableNotListesi = FXCollections.observableArrayList(notListesi);
        filteredNotListesi = new FilteredList<>(observableNotListesi, p -> true);
        notTable.setItems(filteredNotListesi);
        toplamNotLabel.setText("Toplam Not: " + notListesi.size());

        // İlk notu seç (eğer varsa)
        if (!notListesi.isEmpty()) {
            notTable.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void yeniNotEkle() {
        try {
            // Doğru kaynak yolunu kullan
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/notEkle.fxml");
            if (resource == null) {
                System.err.println("notEkle.fxml dosyası bulunamadı!");
                showAlert(Alert.AlertType.ERROR, "Hata", "notEkle.fxml dosyası bulunamadı!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            NotEkleController controller = loader.getController();
            controller.setNotListesi(notListesi);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Yeni Not Ekle");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Tabloyu güncelle
            observableNotListesi.setAll(notListesi);
            filtreUygula();
            toplamNotLabel.setText("Toplam Not: " + notListesi.size());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Not ekleme penceresi açılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void notDuzenle() {
        Not seciliNot = notTable.getSelectionModel().getSelectedItem();
        if (seciliNot == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen düzenlemek için bir not seçin.");
            return;
        }

        try {
            // Doğru kaynak yolunu kullan
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/notDuzenle.fxml");
            if (resource == null) {
                System.err.println("notDuzenle.fxml dosyası bulunamadı!");
                showAlert(Alert.AlertType.ERROR, "Hata", "notDuzenle.fxml dosyası bulunamadı!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            NotDuzenleController controller = loader.getController();
            controller.setNot(seciliNot);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Not Düzenle");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Tabloyu güncelle
            notTable.refresh();
            filtreUygula();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Not düzenleme penceresi açılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void notSil() {
        Not seciliNot = notTable.getSelectionModel().getSelectedItem();
        if (seciliNot == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silmek için bir not seçin.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Not Sil");
        alert.setHeaderText("Not Silme Onayı");
        alert.setContentText("Bu notu silmek istediğinizden emin misiniz?\n\n" + seciliNot.toString());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            notListesi.remove(seciliNot);
            observableNotListesi.remove(seciliNot);
            toplamNotLabel.setText("Toplam Not: " + notListesi.size());
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Not başarıyla silindi.");
        }
    }

    @FXML
    private void durumDegistir() {
        Not seciliNot = notTable.getSelectionModel().getSelectedItem();
        if (seciliNot == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen durumunu değiştirmek için bir not seçin.");
            return;
        }

        seciliNot.setTamamlandi(!seciliNot.isTamamlandi());
        notTable.refresh();
        filtreUygula();

        String durum = seciliNot.isTamamlandi() ? "tamamlandı" : "beklemeye alındı";
        showAlert(Alert.AlertType.INFORMATION, "Durum Değiştirildi", "Not durumu " + durum + ".");
    }

    @FXML
    private void aramaYap() {
        filtreUygula();
    }

    private void filtreUygula() {
        String aramaKelimesi = aramaField.getText().toLowerCase();
        String durumSecimi = durumFiltre.getValue();
        String tipSecimi = tipFiltre.getValue();

        filteredNotListesi.setPredicate(not -> {
            // Arama filtresi
            boolean aramaUygun = true;
            if (aramaKelimesi != null && !aramaKelimesi.isEmpty()) {
                aramaUygun = not.getMusteriAdi().toLowerCase().contains(aramaKelimesi) ||
                        not.getTelefon().toLowerCase().contains(aramaKelimesi) ||
                        not.getNotIcerigi().toLowerCase().contains(aramaKelimesi) ||
                        String.valueOf(not.getNotId()).contains(aramaKelimesi);
            }

            // Durum filtresi
            boolean durumUygun = true;
            if (!"Tümü".equals(durumSecimi)) {
                if ("Bekleyenler".equals(durumSecimi)) {
                    durumUygun = !not.isTamamlandi();
                } else if ("Tamamlananlar".equals(durumSecimi)) {
                    durumUygun = not.isTamamlandi();
                }
            }

            // Tip filtresi
            boolean tipUygun = true;
            if (!"Tümü".equals(tipSecimi)) {
                tipUygun = not.getNotTipi().equals(tipSecimi);
            }

            return aramaUygun && durumUygun && tipUygun;
        });

        toplamNotLabel.setText("Toplam Not: " + filteredNotListesi.size());
    }

    @FXML
    private void kapat() {
        Stage stage = (Stage) aramaField.getScene().getWindow();
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