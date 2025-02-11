package org.example.interfazzcrudjavafx.clases;

public class Productos {
    private int idProducto;
    private String nombreProducto;
    private String categoríaProducto;
    private double precioProducto;
    private boolean disponibilidadProducto;

    public Productos(int idProducto, String nombreProducto, String categoríaProducto, double precioProducto, boolean disponibilidadProducto) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoríaProducto =categoríaProducto;
        this.precioProducto = precioProducto;
        this.disponibilidadProducto = disponibilidadProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoríaProducto() {
        return categoríaProducto;
    }

    public void setCategoríaProducto(String categoríaProducto) {
        this.categoríaProducto = categoríaProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public boolean isDisponibilidadProducto() {
        return disponibilidadProducto;
    }

    public void setDisponibilidadProducto(boolean disponibilidadProducto) {
        this.disponibilidadProducto = disponibilidadProducto;
    }


}
