package com.traffico.augusta.entidades;

import java.io.Serializable;
import java.util.ArrayList;

public class Departamento implements Serializable {

    private int id;
    private String descripcion;
    private ArrayList<Municipio> municipios;

    public Departamento() {
    }

    public Departamento(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
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

    public ArrayList<Municipio> getMunicipios() {
        return municipios;
    }

    public void setMunicipios(ArrayList<Municipio> municipios) {
        this.municipios = municipios;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
