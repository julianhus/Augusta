package com.traffico.manhattan.entidades;

import java.io.Serializable;
import java.util.List;

public class Tienda implements Serializable {

    private int id;
    private String descripcion;
    private String direccion;
    private String coordenadas;
    private Municipio municipio;
    private Mercado mercadoActivo;
    private List<TiendaProducto> tiendaProductos;
    private List<Mercado> mercados;

    public Tienda() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public Mercado getMercadoActivo() {
        return mercadoActivo;
    }

    public void setMercadoActivo(Mercado mercadoActivo) {
        this.mercadoActivo = mercadoActivo;
    }

    public List<TiendaProducto> getTiendaProductos() {
        return tiendaProductos;
    }

    public void setTiendaProductos(List<TiendaProducto> tiendaProductos) {
        this.tiendaProductos = tiendaProductos;
    }

    public List<Mercado> getMercados() {
        return mercados;
    }

    public void setMercados(List<Mercado> mercados) {
        this.mercados = mercados;
    }

    @Override
    public String toString() {
        return descripcion +", "+ direccion +", "+ municipio;
    }
}
