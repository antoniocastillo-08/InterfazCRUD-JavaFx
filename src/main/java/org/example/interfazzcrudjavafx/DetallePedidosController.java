package org.example.interfazzcrudjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetallePedidosController {

    @FXML
    private TableView<DetallePedido> tableView;
    @FXML
    private TableColumn<DetallePedido, Integer> columnId;
    @FXML
    private TableColumn<DetallePedido, Integer> columnIdPedido;
    @FXML
    private TableColumn<DetallePedido, Integer> columnIdProducto;
    @FXML
    private TableColumn<DetallePedido, Integer> columnCantidad;
    @FXML
    private TableColumn<DetallePedido, Double> columnPrecio;
    @FXML
    private TableColumn<DetallePedido, Double> columnSubtotal;

    private ObservableList<DetallePedido> detallesPedidos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas
        columnId.setCellValueFactory(new PropertyValueFactory<>("idDetallePedido"));
        columnIdPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        columnIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        columnCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnPrecio.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
        columnSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Cargar datos
        cargarDatos();
    }

    private void cargarDatos() {
        detallesPedidos.clear();
        String query = "SELECT id_detalle, id_pedido, id_producto, cantidad, precio_producto, (cantidad * precio_producto) AS subtotal FROM detalle_pedidos";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                DetallePedido detalle = new DetallePedido(
                        resultSet.getInt("id_detalle"),
                        resultSet.getInt("id_pedido"),
                        resultSet.getInt("id_producto"),
                        resultSet.getInt("cantidad"),
                        resultSet.getDouble("precio_producto"),
                        resultSet.getDouble("subtotal")
                );
                detallesPedidos.add(detalle);
            }
            tableView.setItems(detallesPedidos);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
