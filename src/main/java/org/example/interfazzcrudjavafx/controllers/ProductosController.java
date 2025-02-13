package org.example.interfazzcrudjavafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.interfazzcrudjavafx.DatabaseConnection;
import org.example.interfazzcrudjavafx.clases.Productos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductosController {

    @FXML private TableView<Productos> tableView;
    @FXML private TableColumn<Productos, Integer> columnId;
    @FXML private TableColumn<Productos, String> columnNombre;
    @FXML private TableColumn<Productos, String> columnCategoria;
    @FXML private TableColumn<Productos, Boolean> columnDisponibilidad;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private CheckBox checkDisponible;
    @FXML private Button btnAgregar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;

    private ObservableList<Productos> listaProductos = FXCollections.observableArrayList();
    private Productos productoSeleccionado = null;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeDB();
        tableView.setOnMouseClicked(this::seleccionarProducto);
    }

    private void configurarColumnas() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        columnCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaProducto"));
        columnDisponibilidad.setCellValueFactory(new PropertyValueFactory<>("disponibilidadProducto"));
    }

    private void cargarDatosDesdeDB() {
        String query = "SELECT * FROM productos";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            listaProductos.clear();
            while (rs.next()) {
                listaProductos.add(new Productos(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponibilidad")
                ));
            }
            tableView.setItems(listaProductos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarProducto() {
        if (txtNombre.getText().isEmpty() || txtCategoria.getText().isEmpty()){
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        String nombre = txtNombre.getText();
        String categoria = txtCategoria.getText();
        boolean disponibilidad = checkDisponible.isSelected();

        String query = "INSERT INTO productos (nombre, categoria, disponibilidad) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setBoolean(3, disponibilidad);
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Producto agregado correctamente.", Alert.AlertType.INFORMATION);
            cargarDatosDesdeDB();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modificarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un producto para modificar.", Alert.AlertType.ERROR);
            return;
        }

        String nombre = txtNombre.getText();
        String categoria = txtCategoria.getText();
        boolean disponibilidad = checkDisponible.isSelected();
        int id = productoSeleccionado.getIdProducto();

        String query = "UPDATE productos SET nombre = ?, categoria = ?, disponibilidad = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, categoria);
            stmt.setBoolean(3, disponibilidad);
            stmt.setInt(4, id);
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Producto modificado correctamente.", Alert.AlertType.INFORMATION);
            cargarDatosDesdeDB();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un producto para eliminar.", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este producto?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "DELETE FROM productos WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, productoSeleccionado.getIdProducto());
                stmt.executeUpdate();

                mostrarAlerta("Éxito", "Producto eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarDatosDesdeDB();
                limpiarCampos();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void seleccionarProducto(MouseEvent event) {
        productoSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            txtId.setText(String.valueOf(productoSeleccionado.getIdProducto()));
            txtNombre.setText(productoSeleccionado.getNombreProducto());
            txtCategoria.setText(productoSeleccionado.getCategoriaProducto());
            checkDisponible.setSelected(productoSeleccionado.isDisponibilidadProducto());
        }
    }

    @FXML
    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtCategoria.clear();
        checkDisponible.setSelected(false);
        productoSeleccionado = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
