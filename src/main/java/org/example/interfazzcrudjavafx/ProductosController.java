package org.example.interfazzcrudjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ProductosController {

    @FXML private TableView<Producto> tableView;
    @FXML private TableColumn<Producto, Integer> columnId;
    @FXML private TableColumn<Producto, String> columnNombre;
    @FXML private TableColumn<Producto, Double> columnPrecio;
    @FXML private TableColumn<Producto, Boolean> columnDisponibilidad;
    @FXML private TableColumn<Producto, String> columnCategoria;

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCategoria;
    @FXML private CheckBox chkDisponibilidad;

    @FXML private Button btnCrear;
    @FXML private Button btnGuardar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnBuscar;
    @FXML private Button btnVolver;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private Producto productoSeleccionado = null;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeDB();

        btnCrear.setOnAction(event -> mostrarCampos());
        btnGuardar.setOnAction(event -> agregarProducto());
        btnModificar.setOnAction(event -> modificarProducto());
        btnEliminar.setOnAction(event -> eliminarProducto());
        btnBuscar.setOnAction(event -> buscarProducto());
        btnVolver.setOnAction(event -> volverAlMenu());

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                txtNombre.setText(newSelection.getNombre());
                txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                chkDisponibilidad.setSelected(newSelection.isDisponible());
                txtCategoria.setText(newSelection.getCategoria()); // Cargar categoría
                mostrarCampos();
            }
        });
    }

    private void configurarColumnas() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnDisponibilidad.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        columnCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria")); // Nueva columna
    }


    private void cargarDatosDesdeDB() {
        String query = "SELECT id, nombre, precio, disponible, categoria FROM productos";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            listaProductos.clear();
            while (rs.next()) {
                listaProductos.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getBoolean("disponible"),
                        rs.getString("categoria") // Agregar categoría
                ));
            }
            tableView.setItems(listaProductos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void mostrarCampos() {
        txtNombre.setVisible(true);
        txtPrecio.setVisible(true);
        txtCategoria.setVisible(true);
        chkDisponibilidad.setVisible(true);
        btnGuardar.setVisible(true);
        btnModificar.setVisible(true);
        btnEliminar.setVisible(true);
    }

    private void ocultarCampos() {
        txtNombre.setVisible(false);
        txtPrecio.setVisible(false);
        txtCategoria.setVisible(false);
        chkDisponibilidad.setVisible(false);
        btnGuardar.setVisible(false);
        btnModificar.setVisible(false);
        btnEliminar.setVisible(false);
        txtNombre.clear();
        txtPrecio.clear();
        chkDisponibilidad.setSelected(false);
    }

    private void agregarProducto() {
        int nuevoId = obtenerProximoIdDisponible();
        String nombre = txtNombre.getText();
        double precio;
        boolean disponible = chkDisponibilidad.isSelected();
        String categoria = txtCategoria.getText().trim(); // Obtener la categoría ingresada

        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            System.out.println("El precio debe ser un número válido.");
            return;
        }

        if (nombre.isEmpty() || categoria.isEmpty()) {
            System.out.println("El nombre y la categoría no pueden estar vacíos.");
            return;
        }

        String insertQuery = "INSERT INTO productos (id, nombre, precio, disponible, categoria) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, nuevoId);
            stmt.setString(2, nombre);
            stmt.setDouble(3, precio);
            stmt.setBoolean(4, disponible);
            stmt.setString(5, categoria);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Producto agregado con ID: " + nuevoId);
                cargarDatosDesdeDB();
                ocultarCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modificarProducto() {
        if (productoSeleccionado == null) {
            System.out.println("Selecciona un producto para modificar");
            return;
        }

        String nombre = txtNombre.getText();
        double precio;
        boolean disponible = chkDisponibilidad.isSelected();
        String categoria = txtCategoria.getText().trim(); // Obtener la categoría

        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            System.out.println("El precio debe ser un número válido.");
            return;
        }

        if (nombre.isEmpty() || categoria.isEmpty()) {
            System.out.println("El nombre y la categoría no pueden estar vacíos.");
            return;
        }

        String updateQuery = "UPDATE productos SET nombre = ?, precio = ?, disponible = ?, categoria = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, nombre);
            stmt.setDouble(2, precio);
            stmt.setBoolean(3, disponible);
            stmt.setString(4, categoria);
            stmt.setInt(5, productoSeleccionado.getId());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Producto modificado correctamente");
                cargarDatosDesdeDB();
                ocultarCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            System.out.println("Selecciona un producto para eliminar");
            return;
        }

        String deleteQuery = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, productoSeleccionado.getId());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Producto eliminado correctamente");
                cargarDatosDesdeDB();
                ocultarCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarProducto() {
        if (!txtBuscar.isVisible()) {
            txtBuscar.setVisible(true);
            txtBuscar.requestFocus();
            return;
        }

        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            txtBuscar.setVisible(false);
            cargarDatosDesdeDB();
            return;
        }

        // Modificamos la consulta para que busque tanto en nombre como en categoría
        String searchQuery = "SELECT id, nombre, precio, disponible, categoria FROM productos " +
                "WHERE nombre LIKE ? OR categoria LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

            stmt.setString(1, "%" + criterio + "%");
            stmt.setString(2, "%" + criterio + "%"); // Para buscar también en la categoría

            ResultSet rs = stmt.executeQuery();
            listaProductos.clear();

            while (rs.next()) {
                listaProductos.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getBoolean("disponible"),
                        rs.getString("categoria") // Agregar la categoría
                ));
            }

            tableView.setItems(listaProductos);
            txtBuscar.setVisible(false);
            txtBuscar.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private int obtenerProximoIdDisponible() {
        String query = "SELECT MIN(t1.id + 1) AS proximo_id " +
                "FROM productos t1 " +
                "LEFT JOIN productos t2 ON t1.id + 1 = t2.id " +
                "WHERE t2.id IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int idDisponible = rs.getInt("proximo_id");
                return idDisponible > 0 ? idDisponible : 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
    @FXML
    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("restaurante-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
