package com.eventia.dto;

import lombok.Data;

@Data
public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol;
}