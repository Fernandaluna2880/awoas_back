package com.geotour.api.repository;

import com.geotour.api.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByDestino_IdOrderByIdDesc(Long destinoId);

    boolean existsByUsuario_IdAndDestino_Id(Long usuarioId, Long destinoId);

    boolean existsByDestino_Id(Long destinoId);
}
