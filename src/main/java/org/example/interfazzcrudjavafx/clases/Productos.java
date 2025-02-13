package org.example.interfazzcrudjavafx.clases;

public class Productos {

    private int idProducto;
    private String nombreProducto;
    private String categoriaProducto;
    private boolean disponibilidadProducto;

    public Productos(int idProducto, String nombreProducto, String categoriaProducto, boolean disponibilidadProducto) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.categoriaProducto = categoriaProducto;
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
    public String getCategoriaProducto() {
        return categoriaProducto;
    }
    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }
    public boolean isDisponibilidadProducto() {
        return disponibilidadProducto;
    }
    public void setDisponibilidadProducto(boolean disponibilidadProducto) {
        this.disponibilidadProducto = disponibilidadProducto;
    }

}
