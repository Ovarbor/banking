package ru.banking.exception;

public class UnAuthException extends RuntimeException {
    public UnAuthException(String message) {
        super(message);
    }
}
