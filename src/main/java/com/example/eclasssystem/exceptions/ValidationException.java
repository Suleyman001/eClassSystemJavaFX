package com.example.eclasssystem.exceptions;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public static void throwIfInvalid(boolean condition, String errorMessage)
            throws ValidationException {
        if (!condition) {
            throw new ValidationException(errorMessage);
        }
    }
}