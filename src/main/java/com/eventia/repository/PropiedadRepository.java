package com.eventia.repository;

import com.eventia.model.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {
    List<Propiedad> findByPropietarioId(Long propietarioId);
    List<Propiedad> findByDisponibleTrue();
}