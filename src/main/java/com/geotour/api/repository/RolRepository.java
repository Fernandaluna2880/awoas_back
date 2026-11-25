package com.geotour.api.repository;

import com.geotour.api.entity.Rol;
import com.geotour.api.entity.enums.ERol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(ERol nombre);
}
