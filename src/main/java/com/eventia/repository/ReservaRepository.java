package com.eventia.repository;

import com.eventia.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioEmail(String email);

    List<Reserva> findByUsuario_Id(Long usuarioId);

    List<Reserva> findByPropiedad_Id(Long propiedadId);
        // <<< NUEVO: reservas por propiedades de un propietario
    List<Reserva> findByPropiedadPropietarioId(Long propietarioId);
}