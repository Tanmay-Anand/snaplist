package com.snaplist_backend.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice //@RestControllerAdvice makes @ExceptionHandler GLOBAL across your entire application.
public class GlobalExceptionHandler {

    // Handling validation errors from @Valid DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)  //@ExceptionHandler tells Spring:
    //“If any controller or service throws this specific exception, don’t crash.
    //Send that exception to this method so I can return a custom response.”
    public ResponseEntity<Map<String, String>> handleValidationError(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        //This method extracts each field error:
        //Example output:
        //{
        //  "title": "Title cannot be empty",
        //  "content": "Content too long"
        //}
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    // Handling validation errors from @Validated (path params / service)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    // Handling your custom ResourceNotFoundException
    //Whenever your code executes:
    //throw new ResourceNotFoundException("Note", id);
    //It becomes:
    //{
    //  "message": "Note with id 10 not found"
    //}
    //With HTTP 404.
    //This is perfect for missing notes, tasks, bookmarks, users, etc.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    // Fallback => This catches everything else: NullPointerException, IllegalStateException, Database errors ,Unexpected bugs
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {

        ex.printStackTrace(); // Logs the error to terminal for debugging.
        // So your clients get a clean error, while YOU get the real details in console.

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Something went wrong. Try again later."));
        //JSON =>
        // { "message": "Something went wrong. Try again later."}
    }
}
