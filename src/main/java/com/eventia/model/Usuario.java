package com.eventia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class Usuario {

    public enum Rol {
        ADMIN,
        PROPIETARIO,
        CLIENTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(name = "verificado")
    private boolean verificado = false;

    @Column(name = "verification_token")
    private String verificationToken;
    
    @Column(name = "foto_perfil")
    private String fotoPerfil;
}