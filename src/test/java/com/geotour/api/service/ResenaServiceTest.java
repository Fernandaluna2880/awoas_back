package com.geotour.api.service;

import com.geotour.api.entity.Destino;
import com.geotour.api.entity.Resena;
import com.geotour.api.entity.Usuario;
import com.geotour.api.mapper.DtoMapper;
import com.geotour.api.repository.DestinoRepository;
import com.geotour.api.repository.ResenaRepository;
import com.geotour.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DestinoRepository destinoRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void eliminarPermiteAlAutor() {
        Usuario autor = Usuario.builder().id(1L).correo("autor@geotour.com").build();
        Resena resena = resenaCon(autor);

        when(resenaRepository.findById(7L)).thenReturn(Optional.of(resena));

        resenaService.eliminar(7L, "autor@geotour.com", false);

        verify(resenaRepository).delete(resena);
    }

    @Test
    void eliminarPermiteAlAdministradorAunqueNoSeaElAutor() {
        Usuario autor = Usuario.builder().id(1L).correo("autor@geotour.com").build();
        Resena resena = resenaCon(autor);

        when(resenaRepository.findById(7L)).thenReturn(Optional.of(resena));

        resenaService.eliminar(7L, "admin@geotour.com", true);

        verify(resenaRepository).delete(resena);
    }

    @Test
    void eliminarRechazaAUnUsuarioDistintoDelAutor() {
        Usuario autor = Usuario.builder().id(1L).correo("autor@geotour.com").build();
        Resena resena = resenaCon(autor);

        when(resenaRepository.findById(7L)).thenReturn(Optional.of(resena));

        assertThatThrownBy(() -> resenaService.eliminar(7L, "otro@geotour.com", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    private Resena resenaCon(Usuario autor) {
        return Resena.builder()
                .id(7L)
                .usuario(autor)
                .destino(Destino.builder().id(10L).nombre("Machu Picchu").build())
                .calificacion(5)
                .comentario("Excelente")
                .build();
    }
}
