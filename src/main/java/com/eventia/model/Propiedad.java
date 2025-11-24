package com.eventia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "propiedad")
public class Propiedad {

    public enum Tipo {
        SALON,
        JARDIN,
        LOCAL,
        HACIENDA,
        OTRO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private Integer capacidad;
    private String descripcion;
    private Double precio;
    private Boolean disponible = true;

    private String imagen;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;
}