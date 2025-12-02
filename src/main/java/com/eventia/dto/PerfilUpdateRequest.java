package com.eventia.dto;

import lombok.Data;

@Data
public class PerfilUpdateRequest {
    private String nombre;
    private String apellido;
    private String fotoPerfil;
}