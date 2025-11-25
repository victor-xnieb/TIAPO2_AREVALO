module org.example.game {
    requires javafx.controls;
    requires javafx.fxml;

    requires google.genai;

    // Paquete donde está MainApp
    opens app to javafx.fxml;
    exports app;

    // Paquete donde están los controladores de FXML
    opens controller to javafx.fxml;
    // (export controller solo si luego lo necesitas desde otros módulos)


}
