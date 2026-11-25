package com.geotour.api.repository;

import com.geotour.api.entity.Destino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinoRepository extends JpaRepository<Destino, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
