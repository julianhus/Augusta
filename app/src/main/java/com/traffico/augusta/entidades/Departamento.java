package com.traffico.augusta.entidades;

import java.io.Serializable;
import java.util.List;

public class Departamento implements Serializable {

    private int id;
    private String descripcion;
    private List<Municipio> municipios;

    public Departamento() {
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

    public List<Municipio> getMunicipios() {
        return municipios;
    }

    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
    }

    @Override
    public String toString() {
        return "Departamento{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", municipios=" + municipios +
                '}';
    }
}
