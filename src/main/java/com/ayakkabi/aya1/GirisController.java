package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class GirisController {
    @FXML
    private TextField kullaniciAdiField;
    @FXML
    private PasswordField sifreField;
    @FXML
    private Text hataMesaji;

    private Main mainApplication;

    // Main sınıfına referans almak için metot
    public void setMainApplication(Main mainApplication) {
        this.mainApplication = mainApplication;
    }

    @FXML
    private void initialize() {
        // Hata mesajını başlangıçta gizle
        hataMesaji.setVisible(false);
    }

    @FXML
    private void girisYap() {
        String email = kullaniciAdiField.getText().trim();
        String sifre = sifreField.getText();

        if (email.isEmpty() || sifre.isEmpty()) {
            showError("Lütfen tüm alanları doldurun.");
            return;
        }

        if (VeriDepolama.kullaniciDogrula(email, sifre)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/main.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) kullaniciAdiField.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Ana ekran yüklenirken bir hata oluştu.");
            }
        } else {
            showError("Geçersiz e-posta veya şifre.");
        }
    }

    @FXML
    private void kayitOl() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) kullaniciAdiField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Kayıt ekranı yüklenirken bir hata oluştu.");
        }
    }

    private void showError(String message) {
        hataMesaji.setText(message);
        hataMesaji.setVisible(true);
    }
}