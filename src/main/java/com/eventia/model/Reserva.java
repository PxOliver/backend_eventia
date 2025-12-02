package com.eventia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reserva")
public class Reserva {

    public enum Estado {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;

    @OneToOne(mappedBy = "reserva")
    @JsonIgnoreProperties("reserva") 
    private Pago pago;

    @JsonProperty("pagado")
    public boolean isPagado() {
        return this.pago != null;
    }
}