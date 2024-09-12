module com.example.assignment_01 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.assignment_01 to javafx.fxml;
    exports com.example.assignment_01;
}