package com.devsuperior.dscatalog.resources.exceptions;

import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(
            ResourceNotFoundException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String errorMessage = "Entity not found";

        return getStandardErrorResponseEntity(request, status, errorMessage, e.getMessage());
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> databaseException(
            DatabaseException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = "Database exception";

        return getStandardErrorResponseEntity(request, status, errorMessage, e.getMessage());
    }

    private ResponseEntity<StandardError> getStandardErrorResponseEntity(
            HttpServletRequest request,
            HttpStatus status,
            String customErrorMessage,
            String exceptionErrorMessage) {
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError(customErrorMessage);
        err.setMessage(exceptionErrorMessage);
        err.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }
}
