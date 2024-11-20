module com.example.eclasssystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.eclasssystem to javafx.fxml;
    exports com.example.eclasssystem;
}