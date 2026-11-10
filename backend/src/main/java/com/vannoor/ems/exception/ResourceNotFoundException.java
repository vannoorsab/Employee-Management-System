package com.vannoor.ems.exception;

/** Thrown when an employee with the requested id does not exist (HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
