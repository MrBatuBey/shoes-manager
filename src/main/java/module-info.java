module com.ayakkabi.aya1 {
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.media;
    requires java.mail; // Bu satırı da ekleyin

    opens com.ayakkabi.aya1 to javafx.fxml;
    exports com.ayakkabi.aya1;
}