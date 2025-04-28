package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;

public class EmailGonderController {
    @FXML
    private TextField aliciEmailField;
    @FXML
    private TextField konuField;
    @FXML
    private TextArea mesajArea;
    @FXML
    private Label dosyaAdiLabel;
    @FXML
    private Button gonderButton;
    @FXML
    private Button iptalButton;

    private File raporDosyasi;

    public void setRaporDosyasi(File raporDosyasi) {
        this.raporDosyasi = raporDosyasi;
        dosyaAdiLabel.setText(raporDosyasi.getName());
    }

    @FXML
    private void gonderEmail() {
        String aliciEmail = aliciEmailField.getText();
        String konu = konuField.getText();
        String mesaj = mesajArea.getText();

        if (aliciEmail.isEmpty() || konu.isEmpty() || mesaj.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun.");
            return;
        }

        try {
            // E-posta gönderme işlemi başlat
            gonderButton.setDisable(true);
            gonderButton.setText("Gönderiliyor...");

            // E-posta gönder
            EmailService.sendEmail(aliciEmail, konu, mesaj, raporDosyasi);

            showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                    "E-posta başarıyla gönderildi: " + aliciEmail);

            gonderButton.setDisable(false);
            gonderButton.setText("Gönder");
            iptal();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "E-posta gönderilirken hata oluştu: " + e.getMessage());
            gonderButton.setDisable(false);
            gonderButton.setText("Gönder");
            e.printStackTrace();
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