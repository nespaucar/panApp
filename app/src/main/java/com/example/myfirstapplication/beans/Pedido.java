package com.example.myfirstapplication.beans;

public class Pedido {
    private int id;
    private String fecha_atencion;
    //private String fecha_programada;
    private String fecha_atencion2;
    private String hora;
    private String turno;
    private String direccion;
    private Float total;
    private String estado;
    private String tipo_pago;
    private String idrepartidor;
    private String latitud;
    private String longitud;

    public Pedido(){}

    public Pedido(int id, String fecha_atencion, String fecha_atencion2, String hora, String turno, String direccion, Float total, String estado, String tipo_pago, String idrepartidor, String latitud, String longitud) {
        this.id = id;
        this.fecha_atencion = fecha_atencion;
        this.fecha_atencion2 = fecha_atencion2;
        this.hora = hora;
        this.turno = turno;
        this.direccion = direccion;
        this.total = total;
        this.estado = estado;
        this.tipo_pago = tipo_pago;
        this.idrepartidor = idrepartidor;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha_atencion() {
        return fecha_atencion;
    }

    public void setFecha_atencion(String fecha_atencion) {
        this.fecha_atencion = fecha_atencion;
    }

    public String getFecha_atencion2() {
        return fecha_atencion2;
    }

    public void setFecha_atencion2(String fecha_atencion2) {
        this.fecha_atencion2 = fecha_atencion2;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo_pago() {
        return tipo_pago;
    }

    public void setTipo_pago(String tipo_pago) {
        this.tipo_pago = tipo_pago;
    }

    public String getIdrepartidor() {
        return idrepartidor;
    }

    public void setIdrepartidor(String idrepartidor) {
        this.idrepartidor = idrepartidor;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
