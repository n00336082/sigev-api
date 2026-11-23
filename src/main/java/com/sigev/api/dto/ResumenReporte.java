package com.sigev.api.dto;

public class ResumenReporte {

    private double totalVentas;
    private long cantidadVentas;
    private double valorInventario;
    private long productosActivos;
    private int unidadesDisponibles;
    private long productosBajoMinimo;
    private long lotesVencidos;
    private int unidadesVencidas;
    private double costoExpuestoVencidos;
    private double costoTotalMermas;
    private long lotesPorVencer;

    public ResumenReporte() {
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public long getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(long cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public double getValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(double valorInventario) {
        this.valorInventario = valorInventario;
    }

    public long getProductosActivos() {
        return productosActivos;
    }

    public void setProductosActivos(long productosActivos) {
        this.productosActivos = productosActivos;
    }

    public int getUnidadesDisponibles() {
        return unidadesDisponibles;
    }

    public void setUnidadesDisponibles(int unidadesDisponibles) {
        this.unidadesDisponibles = unidadesDisponibles;
    }

    public long getProductosBajoMinimo() {
        return productosBajoMinimo;
    }

    public void setProductosBajoMinimo(long productosBajoMinimo) {
        this.productosBajoMinimo = productosBajoMinimo;
    }

    public long getLotesVencidos() {
        return lotesVencidos;
    }

    public void setLotesVencidos(long lotesVencidos) {
        this.lotesVencidos = lotesVencidos;
    }

    public int getUnidadesVencidas() {
        return unidadesVencidas;
    }

    public void setUnidadesVencidas(int unidadesVencidas) {
        this.unidadesVencidas = unidadesVencidas;
    }

    public double getCostoExpuestoVencidos() {
        return costoExpuestoVencidos;
    }

    public void setCostoExpuestoVencidos(double costoExpuestoVencidos) {
        this.costoExpuestoVencidos = costoExpuestoVencidos;
    }

    public double getCostoTotalMermas() {
        return costoTotalMermas;
    }

    public void setCostoTotalMermas(double costoTotalMermas) {
        this.costoTotalMermas = costoTotalMermas;
    }

    public long getLotesPorVencer() {
        return lotesPorVencer;
    }

    public void setLotesPorVencer(long lotesPorVencer) {
        this.lotesPorVencer = lotesPorVencer;
    }
}
