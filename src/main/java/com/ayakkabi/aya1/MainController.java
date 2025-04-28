package com.ayakkabi.aya1;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class MainController {
    private List<Urun> urunListesi;
    private List<Satis> satisListesi;
    private List<Not> notListesi;

    public MainController() {
        // Uygulama başladığında verileri yükle
        urunListesi = VeriDepolama.yukleUrunler();
        satisListesi = VeriDepolama.yukleSatislar();
        notListesi = VeriDepolama.yukleNotlar();
    }

    @FXML
    private void showUrunEkleDialog() {
        try {
            // Resource'un null olup olmadığını kontrol et
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/urunEkle.fxml");
            if (resource == null) {
                System.err.println("urunEkle.fxml dosyası bulunamadı!");
                Alert alert = new Alert(Alert.AlertType.ERROR, "urunEkle.fxml dosyası bulunamadı!");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            UrunEkleController controller = loader.getController();
            controller.setUrunListesi(urunListesi);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ürün Ekle");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Değişiklikleri kaydet
            VeriDepolama.kaydet(urunListesi, satisListesi, notListesi);
        } catch (Exception e) {
            System.err.println("Ürün ekleme penceresi açılırken hata: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void stokGoruntule() {
        try {
            if (urunListesi.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Stokta ürün bulunmamaktadır.");
                alert.setTitle("Stok Görüntüle");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/stokGoruntule.fxml");
            if (resource == null) {
                System.err.println("stokGoruntule.fxml dosyası bulunamadı!");
                Alert alert = new Alert(Alert.AlertType.ERROR, "stokGoruntule.fxml dosyası bulunamadı!");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            StokGoruntuleController controller = loader.getController();
            controller.setUrunListesi(urunListesi);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Stok Görüntüle");
            stage.setScene(new Scene(root, 800, 600));
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Stok görüntüleme penceresi açılırken hata: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void urunDuzenle() {
        try {
            if (urunListesi.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Düzenlenecek ürün bulunmamaktadır.");
                alert.setTitle("Ürün Düzenle");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/urunDuzenle.fxml");
            if (resource == null) {
                System.err.println("urunDuzenle.fxml dosyası bulunamadı!");
                Alert alert = new Alert(Alert.AlertType.ERROR, "urunDuzenle.fxml dosyası bulunamadı!");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            UrunDuzenleController controller = loader.getController();
            controller.setUrunListesi(urunListesi);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ürün Düzenle");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Değişiklikleri kaydet
            VeriDepolama.kaydet(urunListesi, satisListesi, notListesi);
        } catch (Exception e) {
            System.err.println("Ürün düzenleme penceresi açılırken hata: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void satisYap() {
        try {
            if (urunListesi.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Satılacak ürün bulunmamaktadır.");
                alert.setTitle("Satış Yap");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/satisYap.fxml");
            if (resource == null) {
                System.err.println("satisYap.fxml dosyası bulunamadı!");
                Alert alert = new Alert(Alert.AlertType.ERROR, "satisYap.fxml dosyası bulunamadı!");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            SatisYapController controller = loader.getController();
            controller.setData(urunListesi, satisListesi);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Satış Yap");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Değişiklikleri kaydet
            VeriDepolama.kaydet(urunListesi, satisListesi, notListesi);
        } catch (Exception e) {
            System.err.println("Satış penceresi açılırken hata: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void satisRaporu() {
        try {
            if (satisListesi.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Henüz satış kaydı bulunmamaktadır.");
                alert.setTitle("Satış Raporu");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            // Satış raporu penceresi oluştur
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Satış Raporu");
            TableView<Satis> tableView = new TableView<>();
            TableColumn<Satis, Integer> idColumn = new TableColumn<>("Satış ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("satisId"));
            TableColumn<Satis, String> urunColumn = new TableColumn<>("Ürün");
            urunColumn.setCellValueFactory(new PropertyValueFactory<>("urunAdi"));
            TableColumn<Satis, Integer> numaraColumn = new TableColumn<>("Numara");
            numaraColumn.setCellValueFactory(new PropertyValueFactory<>("numara"));
            TableColumn<Satis, Integer> adetColumn = new TableColumn<>("Adet");
            adetColumn.setCellValueFactory(new PropertyValueFactory<>("adet"));
            TableColumn<Satis, Double> birimFiyatColumn = new TableColumn<>("Birim Fiyat");
            birimFiyatColumn.setCellValueFactory(new PropertyValueFactory<>("birimFiyat"));
            birimFiyatColumn.setCellFactory(column -> new TableCell<>() {
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
            TableColumn<Satis, Double> toplamFiyatColumn = new TableColumn<>("Toplam Fiyat");
            toplamFiyatColumn.setCellValueFactory(new PropertyValueFactory<>("toplamFiyat"));
            toplamFiyatColumn.setCellFactory(column -> new TableCell<>() {
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
            TableColumn<Satis, String> tarihColumn = new TableColumn<>("Tarih");
            tarihColumn.setCellValueFactory(new PropertyValueFactory<>("formattedSatisTarihi"));
            TableColumn<Satis, String> musteriColumn = new TableColumn<>("Müşteri");
            musteriColumn.setCellValueFactory(new PropertyValueFactory<>("musteriAdi"));
            tableView.getColumns().addAll(idColumn, urunColumn, numaraColumn, adetColumn,
                    birimFiyatColumn, toplamFiyatColumn, tarihColumn, musteriColumn);
            tableView.setItems(FXCollections.observableArrayList(satisListesi));
            // Toplam satış tutarını hesapla
            double toplamSatisTutari = satisListesi.stream()
                    .mapToDouble(Satis::getToplamFiyat)
                    .sum();
            Label toplamLabel = new Label(String.format("Toplam Satış Tutarı: %.2f TL", toplamSatisTutari));
            toplamLabel.setStyle("-fx-font-weight: bold;");
            Button kapatButton = new Button("Kapat");
            kapatButton.setOnAction(e -> stage.close());
            HBox bottomBox = new HBox(10, toplamLabel, new Region(), kapatButton);
            bottomBox.setPadding(new Insets(10));
            HBox.setHgrow(bottomBox.getChildren().get(1), Priority.ALWAYS);
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(tableView);
            borderPane.setBottom(bottomBox);
            Scene scene = new Scene(borderPane, 800, 600);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Satış raporu görüntülenirken hata oluştu: " + e.getMessage());
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    @FXML
    private void raporGonder() {
        try {
            if (satisListesi.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Henüz satış kaydı bulunmamaktadır.");
                alert.setTitle("Rapor Oluştur");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/raporGonder.fxml");
            if (resource == null) {
                System.err.println("raporGonder.fxml dosyası bulunamadı!");
                Alert alert = new Alert(Alert.AlertType.ERROR, "raporGonder.fxml dosyası bulunamadı!");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            RaporGonderController controller = loader.getController();
            controller.setSatisListesi(satisListesi);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Rapor Oluştur");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Rapor oluşturma penceresi açılırken hata: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void notSistemi() {
        try {
            java.net.URL resource = getClass().getResource("/com/ayakkabi/aya1/notSistemi.fxml");
            if (resource == null) {
                System.err.println("notSistemi.fxml dosyası bulunamadı!");
                Alert alert = new Alert(Alert.AlertType.ERROR, "notSistemi.fxml dosyası bulunamadı!");
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            NotSistemiController controller = loader.getController();
            controller.setNotListesi(notListesi);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Not Sistemi");
            stage.setScene(new Scene(root, 800, 600));
            stage.showAndWait();

            // Değişiklikleri kaydet
            VeriDepolama.kaydet(urunListesi, satisListesi, notListesi);
        } catch (Exception e) {
            System.err.println("Not sistemi penceresi açılırken hata: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Hata: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter metotları
    public List<Urun> getUrunListesi() {
        return urunListesi;
    }

    public List<Satis> getSatisListesi() {
        return satisListesi;
    }

    public List<Not> getNotListesi() {
        return notListesi;
    }
}