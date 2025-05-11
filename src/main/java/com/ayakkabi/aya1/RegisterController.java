package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML
    private TextField adField;
    
    @FXML
    private TextField soyadField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField sifreField;
    
    @FXML
    private PasswordField sifreTekrarField;

    @FXML
    private void handleKayit() {
        String ad = adField.getText().trim();
        String soyad = soyadField.getText().trim();
        String email = emailField.getText().trim();
        String sifre = sifreField.getText();
        String sifreTekrar = sifreTekrarField.getText();

        // Validasyonlar
        if (ad.isEmpty() || soyad.isEmpty() || email.isEmpty() || sifre.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen tüm alanları doldurun.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Geçerli bir e-posta adresi giriniz.");
            return;
        }

        if (!sifre.equals(sifreTekrar)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Şifreler eşleşmiyor.");
            return;
        }

        if (sifre.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Şifre en az 6 karakter olmalıdır.");
            return;
        }

        // E-posta adresi kontrolü
        if (VeriDepolama.kullaniciVarMi(email)) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Bu e-posta adresi zaten kayıtlı.");
            return;
        }

        // Kullanıcıyı kaydet
        VeriDepolama.kaydetKullanici(ad, soyad, email, sifre);

        showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt işlemi başarıyla tamamlandı.");
        handleGiris(); // Giriş ekranına yönlendir
    }

    @FXML
    private void handleGiris() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/giris.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) adField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Giriş ekranı yüklenirken bir hata oluştu.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 