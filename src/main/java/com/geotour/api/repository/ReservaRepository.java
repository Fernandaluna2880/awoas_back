package com.geotour.api.repository;

import com.geotour.api.entity.Reserva;
import com.geotour.api.entity.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuario_CorreoIgnoreCaseOrderByFechaReservaDesc(String correo);

    List<Reserva> findAllByOrderByFechaReservaDesc();

    boolean existsByDestino_Id(Long destinoId);

    @Query("""
            SELECT COALESCE(SUM(r.numeroAsistentes), 0)
            FROM Reserva r
            WHERE r.destino.id = :destinoId
              AND r.fechaReserva = :fecha
              AND r.estado <> :estadoCancelado
            """)
    long sumPersonasReservadas(
            @Param("destinoId") Long destinoId,
            @Param("fecha") LocalDate fecha,
            @Param("estadoCancelado") EstadoReserva estadoCancelado
    );
}
