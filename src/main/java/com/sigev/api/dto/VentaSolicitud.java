package com.sigev.api.dto;

import java.util.List;

public class VentaSolicitud {

    private String cliente;
    private String metodoPago;
    private double montoRecibido;
    private String usuarioResponsable;
    private List<ItemVentaSolicitud> items;

    public VentaSolicitud() {
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public List<ItemVentaSolicitud> getItems() {
        return items;
    }

    public void setItems(List<ItemVentaSolicitud> items) {
        this.items = items;
    }
}
