package org.example.interfazzcrudjavafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class RestauranteController {

    @FXML
    private void irAVentanaClientes(ActionEvent event) {
        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("clientes-view.fxml"));
            Parent root = loader.load();

            // Obtener el escenario actual (ventana) y cambiar la escena
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Cerrar la ventana actual
            stage.close();

            // Mostrar la nueva ventana
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
