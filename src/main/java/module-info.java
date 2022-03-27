module com.example.pocfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;


    opens com.example.pocfx to javafx.fxml;
    exports com.example.pocfx;
}