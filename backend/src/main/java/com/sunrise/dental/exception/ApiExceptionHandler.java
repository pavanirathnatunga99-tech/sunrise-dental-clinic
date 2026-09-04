package com.sunrise.dental.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  public record ErrorBody(Instant timestamp, int status, String message, Map<String, String> fieldErrors) {
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ErrorBody> notFound(NotFoundException e) {
    return body(HttpStatus.NOT_FOUND, e.getMessage(), Map.of());
  }

  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ErrorBody> conflict(ConflictException e) {
    return body(HttpStatus.CONFLICT, e.getMessage(), Map.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e) {
    Map<String, String> fields = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors().forEach(x -> fields.putIfAbsent(x.getField(), x.getDefaultMessage()));
    return body(HttpStatus.BAD_REQUEST, "Please correct the highlighted fields.", fields);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ErrorBody> bad(IllegalArgumentException e) {
    return body(HttpStatus.BAD_REQUEST, e.getMessage(), Map.of());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ErrorBody> integrity(DataIntegrityViolationException e) {
    return body(HttpStatus.CONFLICT, "That value or time slot is already in use. Please choose another.", Map.of());
  }

  private ResponseEntity<ErrorBody> body(HttpStatus s, String m, Map<String, String> f) {
    return ResponseEntity.status(s)
        .body(new ErrorBody(Instant.now(), s.value(), m, f));
  }
}
