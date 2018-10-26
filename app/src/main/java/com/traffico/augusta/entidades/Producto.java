package com.traffico.augusta.entidades;

import java.util.List;

public class Producto {

    private int id;
    private String barCode;
    private String marca;
    private String descripcion;
    private String medida;
    private float valorMedida;
    private List<TiendaProducto> tiendaProductos;

    public Producto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public float getValorMedida() {
        return valorMedida;
    }

    public void setValorMedida(float valorMedida) {
        this.valorMedida = valorMedida;
    }

    public List<TiendaProducto> getTiendaProductos() {
        return tiendaProductos;
    }

    public void setTiendaProductos(List<TiendaProducto> tiendaProductos) {
        this.tiendaProductos = tiendaProductos;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", barCode='" + barCode + '\'' +
                ", marca='" + marca + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", medida='" + medida + '\'' +
                ", valorMedida=" + valorMedida +
                ", tiendaProductos=" + tiendaProductos +
                '}';
    }
}
