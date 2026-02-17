package com.bescobar.notes.shared.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bescobar.notes.shared.exception.dto.ErrorResponse;
import com.bescobar.notes.shared.exception.dto.ValidationErrorResponse;

/**
 * Global exception handler for the entire application.
 * This class intercepts exceptions thrown by REST controllers
 * and transforms them into appropriate HTTP responses with standardized error messages.
 * Uses the exception hierarchy pattern with {@link HttpStatusMapping} annotations
 * to automatically determine the appropriate HTTP status code.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all domain exceptions of the application.
     * Extracts the HTTP status code from the {@link HttpStatusMapping} annotation
     * present in the exception hierarchy.
     *
     * @param ex Domain exception thrown
     * @return HTTP response with formatted error
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
     * Handles invalid data exceptions.
     * Used for custom validations of input data
     * that do not comply with business rules.
     *
     * @param ex Invalid data exception thrown
     * @return HTTP 400 (Bad Request) response with formatted error
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
     * Handles business rule violation exceptions.
     * Used when an operation cannot be completed due to
     * domain restrictions or policies.
     *
     * @param ex Business rule violation exception thrown
     * @return HTTP 422 (Unprocessable Entity) response with formatted error
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
     * Handles Spring Security authorization exceptions.
     * Thrown when an authenticated user does not have the necessary permissions
     * to access a protected resource.
     *
     * @param ex Authorization exception thrown
     * @return HTTP 403 (Forbidden) response with error message
     */
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAuthorizationException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access denied: You don't have permission to access this resource",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handles data access exceptions (database errors).
     * This handler intercepts SQL and persistence errors to avoid
     * exposing internal database details to the client.
     *
     * @param ex Data access exception thrown
     * @return HTTP 500 (Internal Server Error) response with generic message
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        logger.error("Database error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "A database error occurred. Please try again later or contact support.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles generic uncaught runtime exceptions.
     * This method acts as a fallback for unexpected exceptions
     * that do not have a specific handler.
     *
     * @param ex Runtime exception thrown
     * @return HTTP 500 (Internal Server Error) response with generic message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected runtime error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles illegal argument exceptions.
     * Used for basic method argument validations.
     *
     * @param ex Illegal argument exception thrown
     * @return HTTP 400 (Bad Request) response with formatted error
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
     * Handles method argument validation exceptions.
     * Triggered when Bean Validation (@Valid annotations)
     * fail on input DTOs.
     *
     * @param ex Method validation exception thrown
     * @return HTTP 400 (Bad Request) response with detailed error map by field
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
     * Gets the HTTP status code from the {@link HttpStatusMapping} annotation.
     * Recursively traverses the class hierarchy until finding the annotation
     * or returns INTERNAL_SERVER_ERROR as default value.
     *
     * @param exceptionClass Exception class to examine
     * @return HTTP status code associated with the exception
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
