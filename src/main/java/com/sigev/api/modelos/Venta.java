package com.sigev.api.modelos;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroComprobante;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 120)
    private String cliente;

    @Column(nullable = false, length = 30)
    private String metodoPago;

    private double subtotal;

    private double igv;

    private double total;

    private double montoRecibido;

    private double vuelto;

    @Column(nullable = false, length = 100)
    private String usuarioResponsable;

    @Column(nullable = false, length = 20)
    private String estado;

    public Venta() {
    }

    public Venta(Long id, String numeroComprobante, LocalDateTime fechaHora, String cliente,
            String metodoPago, double subtotal, double igv, double total,
            double montoRecibido, double vuelto, String usuarioResponsable, String estado) {
        this.id = id;
        this.numeroComprobante = numeroComprobante;
        this.fechaHora = fechaHora;
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.montoRecibido = montoRecibido;
        this.vuelto = vuelto;
        this.usuarioResponsable = usuarioResponsable;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getVuelto() {
        return vuelto;
    }

    public void setVuelto(double vuelto) {
        this.vuelto = vuelto;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
