package com.bescobar.notes.shared.exception.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Representa la respuesta de error para validaciones de datos.
 * Extiende {@link ErrorResponse} añadiendo un mapa de errores
 * por campo, útil para validaciones de formularios y DTOs.
 */
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> fieldErrors;

    /**
     * Constructor por defecto.
     */
    public ValidationErrorResponse() {
        super();
    }

    /**
     * Constructor con todos los campos.
     *
     * @param status      Código de estado HTTP del error
     * @param message     Mensaje descriptivo del error
     * @param timestamp   Momento en que ocurrió el error
     * @param fieldErrors Mapa de errores por campo (nombre del campo -> mensaje de error)
     */
    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> fieldErrors) {
        super(status, message, timestamp);
        this.fieldErrors = fieldErrors;
    }

    /**
     * Obtiene el mapa de errores por campo.
     *
     * @return Mapa con los errores de validación por campo
     */
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Establece el mapa de errores por campo.
     *
     * @param fieldErrors Mapa con los errores de validación por campo
     */
    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
