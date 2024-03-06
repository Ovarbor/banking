package ru.banking.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundValidationException.class)
    public ResponseEntity<ErrorMessage> notFoundValidationException(NotFoundValidationException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<ErrorMessage> illegalRequestException(IllegalRequestException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> onConstraintValidationException(
            ConstraintViolationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorMessage> onNullPointerException(
            NullPointerException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> conflictException(ConflictException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException exception) {
        final String error = "Unknown " + exception.getName() + ": " + exception.getValue();
        log.warn(error);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Error(error));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(exception.getMessage()));
    }


    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenException(final ForbiddenException e) {
        log.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(e.getMessage()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Error {
        private final String error;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ErrorMessage {
        private final String message;
    }
}

