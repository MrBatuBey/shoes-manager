package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NotDuzenleController {
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
    private CheckBox tamamlandiCheck;
    @FXML
    private Button kaydetButton;
    @FXML
    private Button iptalButton;

    private Not not;

    @FXML
    private void initialize() {
        notTipiCombo.getItems().addAll("Kapora", "Tamir", "Diğer");
    }

    public void setNot(Not not) {
        this.not = not;

        // Form alanlarını doldur
        musteriAdiField.setText(not.getMusteriAdi());
        telefonField.setText(not.getTelefon());
        notTipiCombo.setValue(not.getNotTipi());
        notIcerigiArea.setText(not.getNotIcerigi());
        tutarField.setText(String.format("%.2f", not.getTutar()));
        tamamlandiCheck.setSelected(not.isTamamlandi());

        // Teslim tarihini ayarla
        if (not.getTeslimTarihi() != null) {
            teslimTarihiPicker.setValue(not.getTeslimTarihi().toLocalDate());
        }
    }

    @FXML
    private void notKaydet() {
        String musteriAdi = musteriAdiField.getText();
        String telefon = telefonField.getText();
        String notTipi = notTipiCombo.getValue();
        String notIcerigi = notIcerigiArea.getText();
        String tutarStr = tutarField.getText();
        LocalDate teslimTarihi = teslimTarihiPicker.getValue();
        boolean tamamlandi = tamamlandiCheck.isSelected();

        // Validasyon
        if (musteriAdi.isEmpty() || telefon.isEmpty() || notIcerigi.isEmpty() || tutarStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun.");
            return;
        }

        try {
            double tutar = Double.parseDouble(tutarStr);
            if (tutar < 0) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Tutar negatif olamaz.");
                return;
            }

            // Not bilgilerini güncelle
            not.setMusteriAdi(musteriAdi);
            not.setTelefon(telefon);
            not.setNotTipi(notTipi);
            not.setNotIcerigi(notIcerigi);
            not.setTutar(tutar);
            not.setTamamlandi(tamamlandi);

            // Teslim tarihini güncelle
            if (teslimTarihi != null) {
                LocalDateTime teslimDateTime = LocalDateTime.of(teslimTarihi, LocalTime.of(18, 0));
                not.setTeslimTarihi(teslimDateTime);
            }

            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Not başarıyla güncellendi.");

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