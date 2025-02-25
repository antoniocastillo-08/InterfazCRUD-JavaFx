package org.example.interfazzcrudjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class PedidoController {
    @FXML
    private ChoiceBox<String> choiceClientes, choiceEstado, choiceProducto;
    @FXML
    private Spinner<Integer> spinnerTotal;
    @FXML
    private TableView<Pedido> tableView;
    @FXML
    private TableColumn<Pedido, Integer> columnId, columnIdCliente;
    @FXML
    private TableColumn<Pedido, String> columnFecha, columnHora, columnTotal, columnEstado;
    @FXML
    private Button btnCrear, btnModificar, btnBuscar, btnEliminar, btnMostrarPedidos;

    private ObservableList<Pedido> pedidosList = FXCollections.observableArrayList();

    public void initialize() {
        cargarClientes();
        configurarColumnasTabla();
        cargarProductos();
        configurarChoiceEstado();
        cargarPedidos();
        configurarEventosTabla();
        configurarSpinnerTotal();

        btnCrear.setOnAction(event -> guardarPedido());
        btnModificar.setOnAction(event -> modificarPedido());
        btnBuscar.setOnAction(event -> buscarPedido());
        btnEliminar.setOnAction(event -> eliminarPedido());
        btnMostrarPedidos.setOnAction(event -> abrirDetallesPedidos());
    }

    private void configurarColumnasTabla() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        columnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        columnEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarClientes() {
        choiceClientes.getItems().clear();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre FROM clientes")) {
            while (rs.next()) {
                choiceClientes.getItems().add(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarProductos() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nombre FROM productos")) {
            while (rs.next()) {
                choiceProducto.getItems().add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configurarChoiceEstado() {
        choiceEstado.setItems(FXCollections.observableArrayList("pendiente", "en preparación", "entregado"));
        choiceEstado.setValue("pendiente");
    }

    private void cargarPedidos() {
        ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();
        String consulta = "SELECT id_pedido, id_cliente, fecha, hora, total, estado FROM pedidos";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(consulta);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");
                int idCliente = rs.getInt("id_cliente");
                String fecha = rs.getString("fecha");
                String hora = rs.getString("hora");
                int total = rs.getInt("total");
                String estado = rs.getString("estado");

                listaPedidos.add(new Pedido(idPedido, idCliente, fecha, hora, total, estado));
            }
            tableView.setItems(listaPedidos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarPedido() {
        if (choiceClientes.getValue() == null || choiceEstado.getValue() == null ||
                spinnerTotal.getValue() == null || choiceProducto.getValue() == null) {
            mostrarAlerta("Error", "Faltan datos para crear el pedido. Asegúrate de seleccionar un cliente, producto y estado.");
            return;
        }

        int idCliente = Integer.parseInt(choiceClientes.getValue().split(" - ")[0]);
        String estado = choiceEstado.getValue();
        LocalDate fecha = LocalDate.now();
        LocalTime hora = LocalTime.now();

        int cantidad = spinnerTotal.getValue();

        String productoSeleccionado = choiceProducto.getValue();
        String sqlProducto = "SELECT id, precio FROM productos WHERE nombre = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmtProd = connection.prepareStatement(sqlProducto)) {

            pstmtProd.setString(1, productoSeleccionado);
            ResultSet rsProd = pstmtProd.executeQuery();

            if (rsProd.next()) {
                int idProducto = rsProd.getInt("id");
                double precioProducto = rsProd.getDouble("precio");
                double totalPedido = cantidad * precioProducto;

                // Obtener el próximo id de pedido:
                int nextId = 0;
                String sqlGetMax = "SELECT IFNULL(MAX(id_pedido), 0) AS maxId FROM pedidos";
                try (PreparedStatement pstmtMax = connection.prepareStatement(sqlGetMax)) {
                    ResultSet rsMax = pstmtMax.executeQuery();
                    if (rsMax.next()) {
                        nextId = rsMax.getInt("maxId") + 1;
                    }
                }

                // Insertar en la tabla pedidos
                String sqlPedido = "INSERT INTO pedidos (id_pedido, id_cliente, fecha, hora, total, estado) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmtPedido = connection.prepareStatement(sqlPedido)) {
                    pstmtPedido.setInt(1, nextId);
                    pstmtPedido.setInt(2, idCliente);
                    pstmtPedido.setDate(3, Date.valueOf(fecha));
                    pstmtPedido.setTime(4, Time.valueOf(hora));
                    pstmtPedido.setInt(5, cantidad);
                    pstmtPedido.setString(6, estado);
                    pstmtPedido.executeUpdate();
                }

                // Insertar en la tabla detalle_pedidos
                String sqlDetalle = "INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_producto, subtotal) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmtDetalle = connection.prepareStatement(sqlDetalle)) {
                    pstmtDetalle.setInt(1, nextId);
                    pstmtDetalle.setInt(2, idProducto);
                    pstmtDetalle.setInt(3, cantidad);
                    pstmtDetalle.setDouble(4, precioProducto);
                    pstmtDetalle.setDouble(5, totalPedido); // Guardamos el total en el detalle también
                    pstmtDetalle.executeUpdate();
                }

                // Recargar la tabla de pedidos y reiniciar los controles de la interfaz
                cargarPedidos();
                choiceClientes.setValue(null);
                choiceEstado.setValue("pendiente");
                spinnerTotal.getValueFactory().setValue(0);
                choiceProducto.setValue(null);

                mostrarAlerta("Éxito", "El pedido se ha guardado correctamente.");
            } else {
                mostrarAlerta("Error", "Producto no encontrado en la base de datos.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar el pedido.");
        }
    }


    @FXML
    private void modificarPedido() {
        Pedido pedidoSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un pedido para modificar.");
            return;
        }

        if (choiceClientes.getValue() == null || choiceProducto.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un cliente y un producto.");
            return;
        }

        int idCliente = Integer.parseInt(choiceClientes.getValue().split(" - ")[0]);
        int cantidad = spinnerTotal.getValue();
        String estado = choiceEstado.getValue();
        String productoSeleccionado = choiceProducto.getValue();

        // Obtener el ID del producto seleccionado
        String sqlProducto = "SELECT id, precio FROM productos WHERE nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmtProd = connection.prepareStatement(sqlProducto)) {

            pstmtProd.setString(1, productoSeleccionado);
            ResultSet rsProd = pstmtProd.executeQuery();

            if (rsProd.next()) {
                int idProducto = rsProd.getInt("id");
                double precioProducto = rsProd.getDouble("precio");
                double totalPedido = cantidad * precioProducto;

                // Actualizar la tabla `pedidos`
                String sqlPedido = "UPDATE pedidos SET id_cliente = ?, total = ?, estado = ? WHERE id_pedido = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlPedido)) {
                    pstmt.setInt(1, idCliente);
                    pstmt.setDouble(2, totalPedido);
                    pstmt.setString(3, estado);
                    pstmt.setInt(4, pedidoSeleccionado.getId());
                    pstmt.executeUpdate();
                }

                // Actualizar la tabla `detalle_pedidos`
                String sqlDetalle = "UPDATE detalle_pedidos SET id_producto = ?, cantidad = ?, precio_producto = ?, subtotal = ? WHERE id_pedido = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDetalle)) {
                    pstmt.setInt(1, idProducto);
                    pstmt.setInt(2, cantidad);
                    pstmt.setDouble(3, precioProducto);
                    pstmt.setDouble(4, totalPedido);
                    pstmt.setInt(5, pedidoSeleccionado.getId());
                    pstmt.executeUpdate();
                }

                // Recargar la tabla con los datos actualizados
                cargarPedidos();
                mostrarAlerta("Éxito", "Pedido modificado correctamente.");
            } else {
                mostrarAlerta("Error", "Producto no encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo modificar el pedido.");
        }
    }


    @FXML
    private void buscarPedido() {
        pedidosList.clear();
        String sql = "SELECT * FROM pedidos WHERE id_cliente = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(choiceClientes.getValue().split(" - ")[0]));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pedidosList.add(new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getInt("id_cliente"),
                        rs.getDate("fecha").toString(),
                        rs.getTime("hora").toString(),
                        rs.getInt("total"),
                        rs.getString("estado")
                ));
            }
            tableView.setItems(pedidosList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarPedido() {
        Pedido pedidoSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (pedidoSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un pedido para eliminar.");
            return;
        }

        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, pedidoSeleccionado.getId());
            pstmt.executeUpdate();
            cargarPedidos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void configurarEventosTabla() {
        tableView.setOnMouseClicked(event -> {
            Pedido pedido = tableView.getSelectionModel().getSelectedItem();
            if (pedido != null) {
                // Seleccionar el cliente en el ChoiceBox
                for (String cliente : choiceClientes.getItems()) {
                    if (Integer.parseInt(cliente.split(" - ")[0]) == pedido.getIdCliente()) {
                        choiceClientes.setValue(cliente);
                        break;
                    }
                }

                // Establecer el total y estado
                spinnerTotal.getValueFactory().setValue((int) pedido.getTotal());
                choiceEstado.setValue(pedido.getEstado());

                // Obtener el producto asociado a este pedido desde detalle_pedidos
                String sql = "SELECT p.nombre FROM productos p " +
                        "JOIN detalle_pedidos dp ON p.id = dp.id_producto " +
                        "WHERE dp.id_pedido = ?";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, pedido.getId());
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        choiceProducto.setValue(rs.getString("nombre"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void abrirDetallesPedidos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-pedidos-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Detalles del Pedido");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnMostrarPedidos.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de detalles de pedidos.");
        }
    }


    private void configurarSpinnerTotal() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        spinnerTotal.setValueFactory(valueFactory);
    }
}
