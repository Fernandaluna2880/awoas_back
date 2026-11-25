package com.geotour.api.service;

import com.geotour.api.dto.DestinoDTO;
import com.geotour.api.dto.DestinoResponseDTO;
import com.geotour.api.entity.Destino;
import com.geotour.api.exception.ConflictException;
import com.geotour.api.exception.ResourceNotFoundException;
import com.geotour.api.mapper.DtoMapper;
import com.geotour.api.repository.DestinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DestinoService {

    private final DestinoRepository destinoRepository;
    private final DtoMapper dtoMapper;

    public List<DestinoResponseDTO> listar() {
        return destinoRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre"))
                .stream()
                .map(dtoMapper::toDestinoResponse)
                .toList();
    }

    public DestinoResponseDTO obtener(Long id) {
        return dtoMapper.toDestinoResponse(findById(id));
    }

    @Transactional
    public DestinoResponseDTO crear(DestinoDTO dto) {
        if (destinoRepository.existsByNombreIgnoreCase(dto.nombre().trim())) {
            throw new ConflictException("Ya existe un destino con el nombre indicado");
        }

        Destino destino = Destino.builder()
                .nombre(dto.nombre().trim())
                .descripcion(dto.descripcion().trim())
                .latitud(dto.latitud())
                .longitud(dto.longitud())
                .precioBasePorPersona(dto.precioBasePorPersona())
                .capacidad(dto.capacidad())
                .build();

        return dtoMapper.toDestinoResponse(destinoRepository.save(destino));
    }

    @Transactional
    @CacheEvict(value = "clima", key = "#id")
    public DestinoResponseDTO actualizar(Long id, DestinoDTO dto) {
        Destino destino = findById(id);
        String nuevoNombre = dto.nombre().trim();

        if (!destino.getNombre().equalsIgnoreCase(nuevoNombre)
                && destinoRepository.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new ConflictException("Ya existe un destino con el nombre indicado");
        }

        destino.setNombre(nuevoNombre);
        destino.setDescripcion(dto.descripcion().trim());
        destino.setLatitud(dto.latitud());
        destino.setLongitud(dto.longitud());
        destino.setPrecioBasePorPersona(dto.precioBasePorPersona());
        destino.setCapacidad(dto.capacidad());

        return dtoMapper.toDestinoResponse(destino);
    }

    @Transactional
    @CacheEvict(value = "clima", key = "#id")
    public void eliminar(Long id) {
        Destino destino = findById(id);
        destinoRepository.delete(destino);
    }

    private Destino findById(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destino no encontrado con id " + id
                ));
    }
}
