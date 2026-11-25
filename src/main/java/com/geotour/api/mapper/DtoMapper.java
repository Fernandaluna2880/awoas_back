package com.geotour.api.mapper;

import com.geotour.api.dto.DestinoResponseDTO;
import com.geotour.api.dto.ReservaResponseDTO;
import com.geotour.api.dto.ResenaResponseDTO;
import com.geotour.api.dto.UsuarioResponseDTO;
import com.geotour.api.entity.Destino;
import com.geotour.api.entity.Reserva;
import com.geotour.api.entity.Resena;
import com.geotour.api.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public DestinoResponseDTO toDestinoResponse(Destino destino) {
        return new DestinoResponseDTO(
                destino.getId(),
                destino.getNombre(),
                destino.getDescripcion(),
                destino.getLatitud(),
                destino.getLongitud(),
                destino.getPrecioBasePorPersona(),
                destino.getCapacidad()
        );
    }

    public ReservaResponseDTO toReservaResponse(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getUsuario().getNombreCompleto(),
                reserva.getDestino().getId(),
                reserva.getDestino().getNombre(),
                reserva.getFechaReserva(),
                reserva.getNumeroAsistentes(),
                reserva.getPrecioTotal(),
                reserva.getEstado().name()
        );
    }

    public ResenaResponseDTO toResenaResponse(Resena resena) {
        return new ResenaResponseDTO(
                resena.getId(),
                resena.getDestino().getId(),
                resena.getDestino().getNombre(),
                resena.getUsuario().getId(),
                resena.getUsuario().getNombreCompleto(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFechaCreacion()
        );
    }

    public UsuarioResponseDTO toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                usuario.getRol().getNombre().name()
        );
    }
}
