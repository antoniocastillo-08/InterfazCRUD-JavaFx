package org.example.interfazzcrudjavafx;

public class DetallePedido {
    private int id;
    private int idPedido;
    private int idProducto;
    private int cantidad;
    private double precio_producto;
    private double subtotal;

    public DetallePedido(int id, int idPedido, int idProducto, int cantidad, double precio, double subtotal) {
        this.id = id;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio_producto = precio;
        this.subtotal = subtotal;
    }

    public int getIdDetallePedido() {
        return id;
    }

    public double getPrecioProducto() {
        return precio_producto;
    }



    public int getId() {
        return id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }



    public double getSubtotal() {
        return subtotal;
    }
}
