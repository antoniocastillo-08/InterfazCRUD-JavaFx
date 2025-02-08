package org.example.interfazzcrudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.interfazzcrudjavafx.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteController {
    @FXML private TableView<Cliente> tableView;
    @FXML private TableColumn<Cliente, Integer> columnId;
    @FXML private TableColumn<Cliente, String> columnNombre;
    @FXML private TableColumn<Cliente, String> columnTelefono;
    @FXML private TableColumn<Cliente, String> columnDireccion;


    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private Button btnCrear;
    @FXML private Button btnGuardar;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeDB();

        btnCrear.setOnAction(event -> mostrarCampos());
        btnGuardar.setOnAction(event -> agregarCliente());
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
    }

    private void ocultarCampos() {
        txtNombre.setVisible(false);
        txtTelefono.setVisible(false);
        txtDireccion.setVisible(false);
        btnGuardar.setVisible(false);
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
    }

    private void agregarCliente() {
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String direccion = txtDireccion.getText();

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            System.out.println("Todos los campos son obligatorios");
            return;
        }

        String insertQuery = "INSERT INTO clientes (nombre, telefono, direccion) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, nombre);
            stmt.setString(2, telefono);
            stmt.setString(3, direccion);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Cliente agregado correctamente");
                cargarDatosDesdeDB();  // Recargar los datos en la tabla
                ocultarCampos();  // Ocultar los campos después de guardar
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
