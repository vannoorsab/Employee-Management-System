package com.vannoor.ems.exception;

/** Thrown when another employee already uses the same email (HTTP 409). */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("An employee with email '" + email + "' already exists");
    }
}
