package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
        String kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();

        if (kullaniciAdi.equals("123123") && sifre.equals("123123")) { // Örnek kullanıcı adı ve şifre
            try {
                // Resource'un null olup olmadığını kontrol et
                Stage stage = (Stage) kullaniciAdiField.getScene().getWindow();
                stage.close();
                // Main sınıfındaki metodu kullanarak ana menüyü aç
                if (mainApplication != null) {
                    mainApplication.showMainMenu();
                } else {
                    // Eğer mainApplication null ise, eski yöntemi kullan
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/main.fxml"));
                    Parent root = loader.load();
                    Stage mainStage = new Stage();
                    mainStage.setTitle("Ana Menü");
                    mainStage.setScene(new Scene(root));
                    mainStage.show();
                }
            } catch (Exception e) {
                System.err.println("Ana menü açılırken hata: " + e.getMessage());
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            // Hata mesajını göster
            hataMesaji.setText("Hatalı kullanıcı adı veya şifre!");
            hataMesaji.setVisible(true);
        }
    }
}