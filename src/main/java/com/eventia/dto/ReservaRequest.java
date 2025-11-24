package com.eventia.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaRequest {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long propiedadId;
}