package com.devsuperior.bds04.controllers.exceptions;

import com.devsuperior.bds04.services.exceptions.DataNotFoundException;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<StandardError> dataNotFound(DataNotFoundException e, HttpServletRequest req) {
        StandardError err = new StandardError();
        HttpStatus status = HttpStatus.NOT_FOUND;
        err.setTimeStamp(Instant.now());
        err.setTitle("Data not found exception, please check the documentation.");
        err.setStatus(status.value());
        err.setMessage(e.getMessage());
        err.setPath(req.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> databaseException(DatabaseException e, HttpServletRequest req) {
        StandardError err = new StandardError();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        err.setTimeStamp(Instant.now());
        err.setTitle("Database exception, please check the documentation.");
        err.setStatus(status.value());
        err.setMessage(e.getMessage());
        err.setPath(req.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest req) {
        ValidationExceptionDetails err = new ValidationExceptionDetails();
        status = HttpStatus.UNPROCESSABLE_ENTITY;
        err.setTimeStamp(Instant.now());
        err.setTitle("Method Argument Not Valid Exception, please check the documentation.");
        err.setStatus(status.value());
        err.setMessage(e.getMessage());
        err.setPath(req.getContextPath());

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        fieldErrors.forEach(fieldError -> err.addError(fieldError.getField(), fieldError.getDefaultMessage()));

        return ResponseEntity.status(status).body(err);
    }

}
