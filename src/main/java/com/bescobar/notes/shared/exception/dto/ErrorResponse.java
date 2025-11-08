package com.bescobar.notes.shared.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa la respuesta de error estándar de la API.
 * Esta clase se utiliza para encapsular información sobre errores
 * que ocurren durante el procesamiento de las peticiones HTTP.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
}
