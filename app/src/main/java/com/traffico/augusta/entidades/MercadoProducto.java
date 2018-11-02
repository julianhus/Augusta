package com.traffico.augusta.entidades;

public class MercadoProducto {

    private int id;
    private int cantidad;
    private float total;
    private Mercado mercado;
    private ValorProducto valorProducto;

    public MercadoProducto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public ValorProducto getValorProducto() {
        return valorProducto;
    }

    public void setValorProducto(ValorProducto valorProducto) {
        this.valorProducto = valorProducto;
    }

    @Override
    public String toString() {
        return "MercadoProducto{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", total=" + total +
                //", mercado=" + mercado +
                //", valorProducto=" + valorProducto +
                '}';
    }
}
