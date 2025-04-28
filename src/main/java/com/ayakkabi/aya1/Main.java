package com.ayakkabi.aya1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Giriş ekranıyla başla
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/giris.fxml"));
            Parent root = loader.load();
            // GirisController'a Main referansını ver
            GirisController girisController = loader.getController();
            girisController.setMainApplication(this);

            // Ekran boyutlarını al
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

            // Pencere boyutunu ekran boyutunun %80'i olarak ayarla
            double width = screenBounds.getWidth() * 0.6;
            double height = screenBounds.getHeight() * 0.6;

            primaryStage.setTitle("Şahika Ayakkabı - Giriş");
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);

            // Pencereyi ekranın ortasına yerleştir
            primaryStage.setX((screenBounds.getWidth() - width) / 2);
            primaryStage.setY((screenBounds.getHeight() - height) / 2);

            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Başlangıç hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ana menüyü açmak için metot
    public void showMainMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/main.fxml"));
        Parent root = loader.load();
        // MainController referansını al
        controller = loader.getController();

        // Ekran boyutlarını al
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        // Pencere boyutunu ekran boyutunun %90'ı olarak ayarla
        double width = screenBounds.getWidth() * 0.5;
        double height = screenBounds.getHeight() * 0.5;

        Stage mainStage = new Stage();
        mainStage.setTitle("Şahika Ayakkabı - Ana Menü");
        mainStage.setScene(new Scene(root, width, height));

        // Pencereyi ekranın ortasına yerleştir
        mainStage.setX((screenBounds.getWidth() - width) / 2);
        mainStage.setY((screenBounds.getHeight() - height) / 2);

        mainStage.show();
    }

    @Override
    public void stop() {
        // Uygulama kapanırken verileri kaydet
        if (controller != null) {
            VeriDepolama.kaydet(controller.getUrunListesi(), controller.getSatisListesi(), controller.getNotListesi());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}