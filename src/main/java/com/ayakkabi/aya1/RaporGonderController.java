package com.ayakkabi.aya1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RaporGonderController {
    @FXML
    private ComboBox<String> raporTipiCombo;
    @FXML
    private Button olusturButton;
    @FXML
    private Button emailGonderButton;
    @FXML
    private Button iptalButton;
    private List<Satis> satisListesi;
    private File reportFile;

    @FXML
    private void initialize() {
        raporTipiCombo.getItems().addAll("Günlük", "Haftalık", "Aylık");
        raporTipiCombo.getSelectionModel().selectFirst();
        // Başlangıçta e-posta gönder butonu devre dışı
        emailGonderButton.setDisable(true);
    }

    public void setSatisListesi(List<Satis> satisListesi) {
        this.satisListesi = satisListesi;
    }

    @FXML
    private void olusturRapor() {
        String raporTipi = raporTipiCombo.getValue();
        try {
            // Rapor oluşturma işlemi başlat
            olusturButton.setDisable(true);
            olusturButton.setText("Oluşturuluyor...");
            reportFile = createCSVReport(satisListesi, raporTipi);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                    "Rapor başarıyla oluşturuldu: " + reportFile.getAbsolutePath());
            olusturButton.setDisable(false);
            olusturButton.setText("Oluştur");
            // Rapor oluşturulduktan sonra e-posta gönder butonunu etkinleştir
            emailGonderButton.setDisable(false);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Rapor oluşturulurken hata oluştu: " + e.getMessage());
            olusturButton.setDisable(false);
            olusturButton.setText("Oluştur");
            e.printStackTrace();
        }
    }

    @FXML
    private void emailGonder() {
        if (reportFile == null || !reportFile.exists()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Lütfen önce rapor oluşturun.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ayakkabi/aya1/emailGonder.fxml"));
            Parent root = loader.load();
            EmailGonderController controller = loader.getController();
            controller.setRaporDosyasi(reportFile);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("E-posta Gönder");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "E-posta gönderme penceresi açılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private File createCSVReport(List<Satis> satisListesi, String reportType) throws IOException {
        LocalDate today = LocalDate.now();
        String fileName = "satis_raporu_" + reportType + "_" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv";
        File file = new File(fileName);
        try (FileWriter writer = new FileWriter(file)) {
            // CSV başlık - Kategori sütunu eklendi
            writer.write("Satış ID,Ürün,Kategori,Numara,Adet,Birim Fiyat,Toplam Fiyat,Müşteri,Tarih\n");
            // Filtrelenmiş satış listesi
            List<Satis> filteredSales = filterSalesByReportType(satisListesi, reportType);
            // Tarih biçimlendirme için formatter
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            // CSV satırları
            for (Satis satis : filteredSales) {
                writer.write(String.format("%d,%s,%s,%d,%d,%.2f,%.2f,%s,%s\n",
                        satis.getSatisId(),
                        satis.getUrunAdi().replace(",", ";"),
                        satis.getKategori().replace(",", ";"), // Kategori bilgisi eklendi
                        satis.getNumara(),
                        satis.getAdet(),
                        satis.getBirimFiyat(),
                        satis.getToplamFiyat(),
                        satis.getMusteriAdi() != null ? satis.getMusteriAdi().replace(",", ";") : "",
                        satis.getSatisTarihi().format(dateFormatter)
                ));
            }
            // Toplam satış tutarını ekle
            double toplamTutar = filteredSales.stream().mapToDouble(Satis::getToplamFiyat).sum();
            writer.write(String.format(",,,,,%.2f,TOPLAM,,\n", toplamTutar));
        }
        return file;
    }

    private List<Satis> filterSalesByReportType(List<Satis> satisListesi, String reportType) {
        LocalDateTime now = LocalDateTime.now();
        return satisListesi.stream()
                .filter(satis -> {
                    LocalDateTime satisZamani = satis.getSatisTarihi();
                    switch (reportType.toLowerCase()) {
                        case "günlük":
                            return satisZamani.toLocalDate().equals(now.toLocalDate());
                        case "haftalık":
                            return satisZamani.isAfter(now.minusWeeks(1));
                        case "aylık":
                            return satisZamani.isAfter(now.minusMonths(1));
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
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