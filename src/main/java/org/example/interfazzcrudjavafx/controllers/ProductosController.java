package org.example.interfazzcrudjavafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.interfazzcrudjavafx.DatabaseConnection;
import org.example.interfazzcrudjavafx.clases.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductosController {
    @FXML private TableView<Cliente> tableView;
    @FXML private TableColumn<Cliente, Integer> columnId;
    @FXML private TableColumn<Cliente, String> columnNombre;
    @FXML private TableColumn<Cliente, String> columnCategoría;
    @FXML private TableColumn<Cliente, String> columnDisponibilidad;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private CheckBox chbDisponibilidad;
    @FXML private TextField txtBuscar;

    @FXML private Button btnCrear;
    @FXML private Button btnGuardar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnBuscar;

    private ObservableList<Cliente> listaProductos = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado = null;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeDB();

        btnCrear.setOnAction(event -> mostrarCampos());
        btnGuardar.setOnAction(event -> agregarCliente());
        btnModificar.setOnAction(event -> modificarCliente());
        btnEliminar.setOnAction(event -> eliminarCliente());
        btnBuscar.setOnAction(event -> buscarCliente());

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                txtNombre.setText(newSelection.getNombre());
                txtTelefono.setText(newSelection.getTelefono());
                txtDireccion.setText(newSelection.getDireccion());
                mostrarCampos();
            }
        });
    }

    private void configurarColumnas() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
    }

    private void cargarDatosDesdeDB() {
        String query = "SELECT id, nombre, telefono, direccion FROM clientes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            listaClientes.clear();
            while (rs.next()) {
                listaClientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                ));
            }
            tableView.setItems(listaClientes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarCampos() {
        txtNombre.setVisible(true);
        txtTelefono.setVisible(true);
        txtDireccion.setVisible(true);
        btnGuardar.setVisible(true);
        btnModificar.setVisible(true);
        btnEliminar.setVisible(true);
    }

    private void ocultarCampos() {
        txtNombre.setVisible(false);
        txtTelefono.setVisible(false);
        txtDireccion.setVisible(false);
        btnGuardar.setVisible(false);
        btnModificar.setVisible(false);
        btnEliminar.setVisible(false);
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
    }

    private void agregarCliente() {
        int nuevoId = obtenerProximoIdDisponible();  // Calcula el ID disponible
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String direccion = txtDireccion.getText();

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            System.out.println("Todos los campos son obligatorios");
            return;
        }

        String insertQuery = "INSERT INTO clientes (id, nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, nuevoId);
            stmt.setString(2, nombre);
            stmt.setString(3, telefono);
            stmt.setString(4, direccion);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Cliente agregado con ID: " + nuevoId);
                cargarDatosDesdeDB();
                ocultarCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void modificarCliente() {
        if (clienteSeleccionado == null) {
            System.out.println("Selecciona un cliente para modificar");
            return;
        }

        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String direccion = txtDireccion.getText();

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            System.out.println("Todos los campos son obligatorios");
            return;
        }

        String updateQuery = "UPDATE clientes SET nombre = ?, telefono = ?, direccion = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, nombre);
            stmt.setString(2, telefono);
            stmt.setString(3, direccion);
            stmt.setInt(4, clienteSeleccionado.getId());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente modificado correctamente");
                cargarDatosDesdeDB();
                ocultarCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            System.out.println("Selecciona un cliente para eliminar");
            return;
        }

        String deleteQuery = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, clienteSeleccionado.getId());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Cliente eliminado correctamente");
                cargarDatosDesdeDB();
                ocultarCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarCliente() {
        if (!txtBuscar.isVisible()) {
            // Si el campo de búsqueda no es visible, mostrarlo y salir del método
            txtBuscar.setVisible(true);
            txtBuscar.requestFocus(); // Poner el foco en el campo
            return;
        }

        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            txtBuscar.setVisible(false); // Ocultar el campo si no hay búsqueda
            cargarDatosDesdeDB();
            return;
        }

        String searchQuery = "SELECT id, nombre, telefono, direccion FROM clientes WHERE nombre LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

            stmt.setString(1, "%" + criterio + "%");
            ResultSet rs = stmt.executeQuery();

            listaClientes.clear();
            while (rs.next()) {
                listaClientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("direccion")
                ));
            }
            tableView.setItems(listaClientes);
            txtBuscar.setVisible(false); // Ocultar el campo después de buscar
            txtBuscar.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int obtenerProximoIdDisponible() {
        String query = "SELECT MIN(t1.id + 1) AS proximo_id " +
                "FROM clientes t1 " +
                "LEFT JOIN clientes t2 ON t1.id + 1 = t2.id " +
                "WHERE t2.id IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int idDisponible = rs.getInt("proximo_id");
                return idDisponible > 0 ? idDisponible : 1;  // Si no hay IDs disponibles, empieza desde 1
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;  // En caso de error, empezar desde 1
    }

}
