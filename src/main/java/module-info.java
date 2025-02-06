module org.example.interfazzcrudjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.interfazzcrudjavafx to javafx.fxml;
    exports org.example.interfazzcrudjavafx;
}