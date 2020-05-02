package com.example.myfirstapplication.beans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String marca_id;
    private String unidad_id;
    private String categoria_id;
    private Float precio;
    private Float stock;
    private String dato;
    private Bitmap foto;
    private String ruta;

    public Producto() {
    }

    public Producto(int id, String codigo, String nombre, String descripcion, String marca_id, String unidad_id, String categoria_id, Float precio, Float stock, String dato, Bitmap foto, String ruta) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca_id = marca_id;
        this.unidad_id = unidad_id;
        this.categoria_id = categoria_id;
        this.precio = precio;
        this.stock = stock;
        this.dato = dato;
        this.foto = foto;
        this.ruta = ruta;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String id) {
        this.codigo = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca_id() {
        return marca_id;
    }

    public void setMarca_id(String marca_id) {
        this.marca_id = marca_id;
    }

    public String getUnidad_id() {
        return unidad_id;
    }

    public void setUnidad_id(String unidad_id) {
        this.unidad_id = unidad_id;
    }

    public String getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(String categoria_id) {
        this.categoria_id = categoria_id;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Float getStock() {
        return stock;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;

        try {
            byte[] bytedato = Base64.decode(dato, Base64.DEFAULT);
            int alto = 100;
            int ancho = 100;
            Bitmap fotx = BitmapFactory.decodeByteArray(bytedato, 0, bytedato.length);
            this.foto = Bitmap.createScaledBitmap(fotx, alto, ancho, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
