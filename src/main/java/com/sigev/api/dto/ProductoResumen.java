package com.sigev.api.dto;

public class ProductoResumen {

    private Long id;
    private String codigo;
    private String nombre;
    private String categoria;
    private String unidadMedida;
    private double precioVenta;
    private double costoUnitario;
    private int stockMinimo;
    private String descripcion;
    private boolean activo;
    private Long ubicacionId;
    private String ubicacionCodigo;
    private String ubicacionNombre;
    private int stockVigente;
    private int stockVencido;
    private boolean bajoMinimo;

    public ProductoResumen() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(Long ubicacionId) {
        this.ubicacionId = ubicacionId;
    }

    public String getUbicacionCodigo() {
        return ubicacionCodigo;
    }

    public void setUbicacionCodigo(String ubicacionCodigo) {
        this.ubicacionCodigo = ubicacionCodigo;
    }

    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }

    public int getStockVigente() {
        return stockVigente;
    }

    public void setStockVigente(int stockVigente) {
        this.stockVigente = stockVigente;
    }

    public int getStockVencido() {
        return stockVencido;
    }

    public void setStockVencido(int stockVencido) {
        this.stockVencido = stockVencido;
    }

    public boolean isBajoMinimo() {
        return bajoMinimo;
    }

    public void setBajoMinimo(boolean bajoMinimo) {
        this.bajoMinimo = bajoMinimo;
    }
}
