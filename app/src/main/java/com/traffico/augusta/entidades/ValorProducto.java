package com.traffico.augusta.entidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ValorProducto {

    private int id;
    private float valor;
    private float valorEquivalente;
    private Date fechaRegistro;
    private TiendaProducto idTiendaProducto;
    private List<MercadoProducto> mercadoProductos;

    public ValorProducto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public TiendaProducto getIdTiendaProducto() {
        return idTiendaProducto;
    }

    public void setIdTiendaProducto(TiendaProducto idTiendaProducto) {
        this.idTiendaProducto = idTiendaProducto;
    }

    public List<MercadoProducto> getMercadoProductos() {
        return mercadoProductos;
    }

    public void setMercadoProductos(List<MercadoProducto> mercadoProductos) {
        this.mercadoProductos = mercadoProductos;
    }

    @Override
    public String toString() {
        //
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        String dateTime = dateFormat.format(fechaRegistro);
        //
        return "Valor $" + valor + "\n"
                + idTiendaProducto.getProducto().getMedida() + " $" + valorEquivalente +
                "\n Fecha " + dateTime;
    }
}
