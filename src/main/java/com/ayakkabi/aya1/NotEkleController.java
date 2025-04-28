package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class NotEkleController {
    @FXML
    private TextField musteriAdiField;
    @FXML
    private TextField telefonField;
    @FXML
    private ComboBox<String> notTipiCombo;
    @FXML
    private TextArea notIcerigiArea;
    @FXML
    private TextField tutarField;
    @FXML
    private DatePicker teslimTarihiPicker;
    @FXML
    private Button kaydetButton;
    @FXML
    private Button iptalButton;

    private List<Not> notListesi;

    @FXML
    private void initialize() {
        notTipiCombo.getItems().addAll("Kapora", "Tamir", "Diğer");
        notTipiCombo.getSelectionModel().selectFirst();

        // Bugünün tarihini varsayılan olarak ayarla
        teslimTarihiPicker.setValue(LocalDate.now().plusDays(1));
    }

    public void setNotListesi(List<Not> notListesi) {
        this.notListesi = notListesi;
    }

    @FXML
    private void notKaydet() {
        String musteriAdi = musteriAdiField.getText();
        String telefon = telefonField.getText();
        String notTipi = notTipiCombo.getValue();
        String notIcerigi = notIcerigiArea.getText();
        String tutarStr = tutarField.getText();
        LocalDate teslimTarihi = teslimTarihiPicker.getValue();

        // Validasyon
        if (musteriAdi.isEmpty() || telefon.isEmpty() || notIcerigi.isEmpty() || tutarStr.isEmpty() || teslimTarihi == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun.");
            return;
        }

        try {
            double tutar = Double.parseDouble(tutarStr);
            if (tutar < 0) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Tutar negatif olamaz.");
                return;
            }

            // LocalDate'i LocalDateTime'a çevir (saat 18:00 olarak ayarla)
            LocalDateTime teslimDateTime = LocalDateTime.of(teslimTarihi, LocalTime.of(18, 0));

            // Yeni not oluştur
            Not yeniNot = new Not(musteriAdi, telefon, notTipi, notIcerigi, tutar, teslimDateTime);
            notListesi.add(yeniNot);

            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Not başarıyla kaydedildi.");

            // Pencereyi kapat
            iptal();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen geçerli bir tutar girin.");
        }
    }

    @FXML
    private void iptal() {
        Stage stage = (Stage) iptalButton.getScene().getWindow();
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