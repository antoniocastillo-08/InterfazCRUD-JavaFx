module org.example.interfazzcrudjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.interfazzcrudjavafx to javafx.fxml;
    exports org.example.interfazzcrudjavafx;
}