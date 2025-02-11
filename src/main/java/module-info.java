module org.example.interfazzcrudjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.interfazzcrudjavafx to javafx.fxml;
    exports org.example.interfazzcrudjavafx;
    exports org.example.interfazzcrudjavafx.clases;
    opens org.example.interfazzcrudjavafx.clases to javafx.fxml;
    exports org.example.interfazzcrudjavafx.controllers;
    opens org.example.interfazzcrudjavafx.controllers to javafx.fxml;
}