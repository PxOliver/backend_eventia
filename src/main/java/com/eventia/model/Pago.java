package com.eventia.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pago")
public class Pago {

    public enum Metodo {
        EFECTIVO,
        TARJETA,
        YAPE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double monto;
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private Metodo metodo;

    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;
}