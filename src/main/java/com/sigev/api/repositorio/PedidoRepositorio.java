package com.sigev.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigev.api.modelos.Pedido;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByFechaEmisionDescIdDesc();
}
