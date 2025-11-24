package com.eventia.service;

import com.eventia.model.Propiedad;
import com.eventia.model.Reserva;
import com.eventia.model.Usuario;
import com.eventia.repository.PropiedadRepository;
import com.eventia.repository.ReservaRepository;
import com.eventia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public java.util.Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva saveReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public boolean existsById(Long id) {
        return reservaRepository.existsById(id);
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public Reserva createReserva(LocalDate fechaInicio, LocalDate fechaFin, Long propiedadId) {

        LocalDate hoy = LocalDate.now();
        if (fechaInicio.isBefore(hoy) || fechaFin.isBefore(hoy)) {
            throw new RuntimeException("No se permiten fechas pasadas para la reserva.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new RuntimeException("La fecha fin no puede ser anterior a la fecha inicio.");
        }
 
        String emailUsuario = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + emailUsuario));

        Propiedad propiedad = propiedadRepository.findById(propiedadId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada con ID: " + propiedadId));

        Reserva reserva = new Reserva();
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setUsuario(usuario);
        reserva.setPropiedad(propiedad);
        reserva.setEstado(Reserva.Estado.PENDIENTE); // Inicialmente pendiente

        return reservaRepository.save(reserva);
    }

    public List<Reserva> findByClienteEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return reservaRepository.findByUsuario_Id(usuario.getId());
    }

    public List<Reserva> findByPropietarioEmail(String email) {
        Usuario propietario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado con email: " + email));
        return reservaRepository.findByPropiedadPropietarioId(propietario.getId());
    }

    public Reserva cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));

        reserva.setEstado(Reserva.Estado.CANCELADA); // ✅ Usar enum
        return reservaRepository.save(reserva);
    }

    public Reserva confirmarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));

        reserva.setEstado(Reserva.Estado.CONFIRMADA); // ✅ Usar enum
        return reservaRepository.save(reserva);
    }
}