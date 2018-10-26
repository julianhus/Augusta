package com.traffico.augusta.entidades;

public class TiendaProducto {

    private int id;
    Tienda tienda;
    Producto producto;

    public TiendaProducto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "TiendaProducto{" +
                "id=" + id +
                ", tienda=" + tienda +
                ", producto=" + producto +
                '}';
    }
}
