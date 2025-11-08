package com.bescobar.notes.shared.exception;

import com.bescobar.notes.shared.exception.dto.ErrorResponse;
import com.bescobar.notes.shared.exception.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Esta clase intercepta las excepciones lanzadas por los controladores REST
 * y las transforma en respuestas HTTP apropiadas con mensajes de error estandarizados.
 * Utiliza el patrón de jerarquía de excepciones con anotaciones {@link HttpStatusMapping}
 * para determinar automáticamente el código de estado HTTP apropiado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja todas las excepciones de dominio de la aplicación.
     * Extrae el código de estado HTTP de la anotación {@link HttpStatusMapping}
     * presente en la jerarquía de la excepción.
     *
     * @param ex Excepción de dominio lanzada
     * @return Respuesta HTTP con el error formateado
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        HttpStatus status = getHttpStatusFromAnnotation(ex.getClass());
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones de datos inválidos.
     * Se utiliza para validaciones personalizadas de datos de entrada
     * que no cumplen con las reglas de negocio.
     *
     * @param ex Excepción de datos inválidos lanzada
     * @return Respuesta HTTP 400 (Bad Request) con el error formateado
     */
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataException(InvalidDataException ex) {
        HttpStatus status = getHttpStatusFromAnnotation(ex.getClass());
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones de violación de reglas de negocio.
     * Se utiliza cuando una operación no puede completarse debido a
     * restricciones o políticas del dominio.
     *
     * @param ex Excepción de violación de regla de negocio lanzada
     * @return Respuesta HTTP 422 (Unprocessable Entity) con el error formateado
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        HttpStatus status = getHttpStatusFromAnnotation(ex.getClass());
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja excepciones genéricas de tiempo de ejecución no capturadas.
     * Este método actúa como un fallback para excepciones inesperadas
     * que no tienen un manejador específico.
     *
     * @param ex Excepción de tiempo de ejecución lanzada
     * @return Respuesta HTTP 500 (Internal Server Error) con mensaje genérico
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Maneja excepciones de argumentos ilegales.
     * Se utiliza para validaciones básicas de argumentos de métodos.
     *
     * @param ex Excepción de argumento ilegal lanzada
     * @return Respuesta HTTP 400 (Bad Request) con el error formateado
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones de validación de argumentos de métodos.
     * Se activa cuando las validaciones de Bean Validation (anotaciones @Valid)
     * fallan en los DTOs de entrada.
     *
     * @param ex Excepción de validación de método lanzada
     * @return Respuesta HTTP 400 (Bad Request) con mapa detallado de errores por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Obtiene el código de estado HTTP de la anotación {@link HttpStatusMapping}.
     * Recorre recursivamente la jerarquía de clases hasta encontrar la anotación
     * o devuelve INTERNAL_SERVER_ERROR como valor por defecto.
     *
     * @param exceptionClass Clase de la excepción a examinar
     * @return Código de estado HTTP asociado a la excepción
     */
    private HttpStatus getHttpStatusFromAnnotation(Class<?> exceptionClass) {
        HttpStatusMapping annotation = exceptionClass.getAnnotation(HttpStatusMapping.class);
        if (annotation != null) {
            return annotation.value();
        }

        Class<?> superClass = exceptionClass.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            return getHttpStatusFromAnnotation(superClass);
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
