package com.example.myfirstapplication.beans;

public class Objeto {
    private int id;
    private String codigo;
    private String nombre;
    private int marca_id;
    private int unidad_id;
    private int categoria_id;
    private Double precio;

    public Objeto(int id, String codigo, String nombre, int marca_id, int unidad_id, int categoria_id, Double precio) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca_id = marca_id;
        this.unidad_id = unidad_id;
        this.categoria_id = categoria_id;
        this.precio = precio;
    }

    public Objeto() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getMarca_id() {
        return marca_id;
    }

    public void setMarca_id(int marca_id) {
        this.marca_id = marca_id;
    }

    public int getUnidad_id() {
        return unidad_id;
    }

    public void setUnidad_id(int unidad_id) {
        this.unidad_id = unidad_id;
    }

    public int getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(int categoria_id) {
        this.categoria_id = categoria_id;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
