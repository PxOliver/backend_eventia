package com.eventia.controller;

import com.eventia.dto.ReservaRequest;
import com.eventia.model.Reserva;
import com.eventia.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reserva> getAllReservas() {
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long id) {
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<Reserva>> getReservasDelCliente(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        List<Reserva> reservas = reservaService.findByClienteEmail(user.getUsername());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/propietario")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<List<Reserva>> getReservasDelPropietario(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        List<Reserva> reservas = reservaService.findByPropietarioEmail(user.getUsername());
        return ResponseEntity.ok(reservas);
    }
   

    @PostMapping
    public ResponseEntity<Reserva> createReserva(@RequestBody ReservaRequest request) {
        Reserva reserva = reservaService.createReserva(
                request.getFechaInicio(),
                request.getFechaFin(),
                request.getPropiedadId()
        );
        return ResponseEntity.ok(reserva);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelarReserva(@PathVariable Long id) {
        try {
            Reserva reservaCancelada = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(reservaCancelada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Reserva> confirmarReserva(@PathVariable Long id) {
        try {
            Reserva reservaConfirmada = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(reservaConfirmada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Reserva> rechazarReserva(@PathVariable Long id) {
        try {
            Reserva reservaRechazada = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(reservaRechazada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
   
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        if (reservaService.existsById(id)) {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}