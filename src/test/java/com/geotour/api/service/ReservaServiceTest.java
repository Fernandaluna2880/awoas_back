package com.geotour.api.service;

import com.geotour.api.dto.ReservaDTO;
import com.geotour.api.dto.ReservaResponseDTO;
import com.geotour.api.entity.Destino;
import com.geotour.api.entity.Reserva;
import com.geotour.api.entity.Usuario;
import com.geotour.api.entity.enums.EstadoReserva;
import com.geotour.api.mapper.DtoMapper;
import com.geotour.api.repository.DestinoRepository;
import com.geotour.api.repository.ReservaRepository;
import com.geotour.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DestinoRepository destinoRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearCalculaPrecioTotalYCantidadDeAsistentes() {
        Usuario usuario = Usuario.builder().id(1L).correo("user@geotour.com").build();
        Destino destino = Destino.builder()
                .id(10L)
                .nombre("Machu Picchu")
                .precioBasePorPersona(new BigDecimal("250.00"))
                .capacidad(20)
                .build();
        ReservaDTO dto = new ReservaDTO(10L, LocalDate.now().plusDays(1), 3);

        when(usuarioRepository.findByCorreoIgnoreCase("user@geotour.com"))
                .thenReturn(Optional.of(usuario));
        when(destinoRepository.findById(10L)).thenReturn(Optional.of(destino));
        when(reservaRepository.sumPersonasReservadas(any(), any(), any())).thenReturn(0L);
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(dtoMapper.toReservaResponse(any(Reserva.class)))
                .thenReturn(new ReservaResponseDTO(1L, 1L, "Usuario", 10L, "Machu Picchu",
                        dto.fechaReserva(), 3, new BigDecimal("750.00"), "PENDIENTE"));

        ReservaResponseDTO response = reservaService.crear(dto, "user@geotour.com");

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());
        assertThat(captor.getValue().getNumeroAsistentes()).isEqualTo(3);
        assertThat(captor.getValue().getPrecioTotal()).isEqualByComparingTo("750.00");
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoReserva.PENDIENTE);
        assertThat(response.precioTotal()).isEqualByComparingTo("750.00");
    }

    @Test
    void cancelarPermiteAUnAdministradorAunqueNoSeaElDueno() {
        Usuario otro = Usuario.builder().id(2L).correo("otro@geotour.com").build();
        Destino destino = Destino.builder().id(10L).nombre("Machu Picchu").build();
        Reserva reserva = Reserva.builder()
                .id(5L)
                .usuario(otro)
                .destino(destino)
                .estado(EstadoReserva.PENDIENTE)
                .build();

        when(reservaRepository.findById(5L)).thenReturn(Optional.of(reserva));

        reservaService.cancelar(5L, "admin@geotour.com", true);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void cancelarRechazaAUnUsuarioQueNoEsElDueno() {
        Usuario otro = Usuario.builder().id(2L).correo("otro@geotour.com").build();
        Reserva reserva = Reserva.builder()
                .id(5L)
                .usuario(otro)
                .estado(EstadoReserva.PENDIENTE)
                .build();

        when(reservaRepository.findById(5L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelar(5L, "user@geotour.com", false))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.PENDIENTE);
    }
}
