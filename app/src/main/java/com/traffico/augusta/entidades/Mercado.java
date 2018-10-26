package com.traffico.augusta.entidades;

import java.util.Date;
import java.util.List;

public class Mercado {

    private int id;
    private boolean estadoMercado;
    private Date fechaRegistro;
    private Tienda tienda;
    private Usuario usuario;
    private List<MercadoProducto> mercadoProductos;

    public Mercado() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEstadoMercado() {
        return estadoMercado;
    }

    public void setEstadoMercado(boolean estadoMercado) {
        this.estadoMercado = estadoMercado;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<MercadoProducto> getMercadoProductos() {
        return mercadoProductos;
    }

    public void setMercadoProductos(List<MercadoProducto> mercadoProductos) {
        this.mercadoProductos = mercadoProductos;
    }

    @Override
    public String toString() {
        return "Mercado{" +
                "id=" + id +
                ", estadoMercado=" + estadoMercado +
                ", fechaRegistro=" + fechaRegistro +
                ", tienda=" + tienda +
                ", usuario=" + usuario +
                ", mercadoProductos=" + mercadoProductos +
                '}';
    }
}
