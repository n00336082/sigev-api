package com.sigev.api.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sigev.api.modelos.Producto;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    List<Producto> findByActivoTrueOrderByNombreAsc();

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%')) ORDER BY p.nombre ASC")
    List<Producto> buscarPorTexto(@Param("texto") String texto);
}
