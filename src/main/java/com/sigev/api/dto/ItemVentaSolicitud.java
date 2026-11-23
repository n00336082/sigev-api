package com.sigev.api.dto;

public class ItemVentaSolicitud {

    private Long productoId;
    private int cantidad;

    public ItemVentaSolicitud() {
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
