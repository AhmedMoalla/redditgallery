package com.novencia.bltech.users.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email: " + email + " already exists.");
    }
}
