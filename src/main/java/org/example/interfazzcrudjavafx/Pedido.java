package org.example.interfazzcrudjavafx;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Pedido {
    private final IntegerProperty id;
    private final IntegerProperty idCliente;
    private final ObjectProperty<LocalDate> fecha;
    private final ObjectProperty<LocalTime> hora;
    private final DoubleProperty total;
    private final StringProperty estado;

    // Constructor sin fecha y hora (se asignan automáticamente)
    public Pedido(int id, int idCliente, String fecha, String hora, double total, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.fecha = new SimpleObjectProperty<>(LocalDate.parse(fecha)); // Convierte el String a LocalDate
        this.hora = new SimpleObjectProperty<>(LocalTime.parse(hora)); // Convierte el String a LocalTime
        this.total = new SimpleDoubleProperty(total);
        this.estado = new SimpleStringProperty(estado);
    }


    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getIdCliente() {
        return idCliente.get();
    }

    public IntegerProperty idClienteProperty() {
        return idCliente;
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora.get();
    }

    public ObjectProperty<LocalTime> horaProperty() {
        return hora;
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }
}
