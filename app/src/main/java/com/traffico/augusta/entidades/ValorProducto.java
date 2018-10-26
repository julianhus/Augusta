package com.traffico.augusta.entidades;

import java.util.Date;
import java.util.List;

public class ValorProducto {

    private TiendaProducto idTiendaProducto;
    private float valor;
    private float valorEquivalente;
    private Date fechaRegistro;
    private List<MercadoProducto> mercadoProductos;

    public ValorProducto() {
    }

    public TiendaProducto getIdTiendaProducto() {
        return idTiendaProducto;
    }

    public void setIdTiendaProducto(TiendaProducto idTiendaProducto) {
        this.idTiendaProducto = idTiendaProducto;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public float getValorEquivalente() {
        return valorEquivalente;
    }

    public void setValorEquivalente(float valorEquivalente) {
        this.valorEquivalente = valorEquivalente;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<MercadoProducto> getMercadoProductos() {
        return mercadoProductos;
    }

    public void setMercadoProductos(List<MercadoProducto> mercadoProductos) {
        this.mercadoProductos = mercadoProductos;
    }

    @Override
    public String toString() {
        return "ValorProducto{" +
                "idTiendaProducto=" + idTiendaProducto +
                ", valor=" + valor +
                ", valorEquivalente=" + valorEquivalente +
                ", fechaRegistro=" + fechaRegistro +
                ", mercadoProductos=" + mercadoProductos +
                '}';
    }
}
