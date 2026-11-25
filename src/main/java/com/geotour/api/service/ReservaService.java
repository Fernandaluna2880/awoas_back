package com.geotour.api.service;

import com.geotour.api.dto.ReservaDTO;
import com.geotour.api.dto.ReservaResponseDTO;
import com.geotour.api.entity.Destino;
import com.geotour.api.entity.Reserva;
import com.geotour.api.entity.Usuario;
import com.geotour.api.entity.enums.EstadoReserva;
import com.geotour.api.exception.BadRequestException;
import com.geotour.api.exception.ConflictException;
import com.geotour.api.exception.ResourceNotFoundException;
import com.geotour.api.mapper.DtoMapper;
import com.geotour.api.repository.DestinoRepository;
import com.geotour.api.repository.ReservaRepository;
import com.geotour.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public ReservaResponseDTO crear(ReservaDTO dto, String correoUsuario) {
        Usuario usuario = findUsuario(correoUsuario);
        Destino destino = findDestino(dto.destinoId());

        if (dto.numeroAsistentes() > destino.getCapacidad()) {
            throw new BadRequestException(
                    "El numero de personas excede la capacidad del destino"
            );
        }

        long personasReservadas = reservaRepository.sumPersonasReservadas(
                destino.getId(),
                dto.fechaReserva(),
                EstadoReserva.CANCELADA
        );

        if (personasReservadas + dto.numeroAsistentes() > destino.getCapacidad()) {
            throw new ConflictException(
                    "No hay disponibilidad suficiente para la fecha seleccionada"
            );
        }

        BigDecimal precioTotal = destino.getPrecioBasePorPersona()
                .multiply(BigDecimal.valueOf(dto.numeroAsistentes()))
                .setScale(2, RoundingMode.HALF_UP);

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .destino(destino)
                .fechaReserva(dto.fechaReserva())
                .numeroAsistentes(dto.numeroAsistentes())
                .precioTotal(precioTotal)
                .estado(EstadoReserva.PENDIENTE)
                .build();

        return dtoMapper.toReservaResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarMisReservas(String correoUsuario) {
        return reservaRepository
                .findByUsuario_CorreoIgnoreCaseOrderByFechaReservaDesc(correoUsuario)
                .stream()
                .map(dtoMapper::toReservaResponse)
                .toList();
    }

    @Transactional
    public ReservaResponseDTO cancelar(Long id, String correoUsuario, boolean esAdmin) {
        Reserva reserva = findById(id);

        if (!esAdmin
                && !reserva.getUsuario().getCorreo().equalsIgnoreCase(correoUsuario)) {
            throw new AccessDeniedException(
                    "No puede cancelar una reserva que pertenece a otro usuario"
            );
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BadRequestException("La reserva ya se encuentra cancelada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        return dtoMapper.toReservaResponse(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAllByOrderByFechaReservaDesc()
                .stream()
                .map(dtoMapper::toReservaResponse)
                .toList();
    }

    @Transactional
    public void eliminar(Long id) {
        Reserva reserva = findById(id);
        reservaRepository.delete(reserva);
    }

    private Reserva findById(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reserva no encontrada con id " + id
                ));
    }

    private Usuario findUsuario(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con correo " + correo
                ));
    }

    private Destino findDestino(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destino no encontrado con id " + id
                ));
    }
}
