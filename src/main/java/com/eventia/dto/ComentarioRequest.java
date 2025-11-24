package com.eventia.dto;

import lombok.Data;

@Data
public class ComentarioRequest {
    private String contenido;
    private int calificacion;
    private Long usuario_id;
    private Long propiedad_id;
}