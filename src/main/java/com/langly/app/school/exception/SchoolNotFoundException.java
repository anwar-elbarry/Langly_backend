package com.langly.app.school.exception;

public class SchoolNotFoundException extends RuntimeException {

    public SchoolNotFoundException(String message) {
        super(message);
    }

    public SchoolNotFoundException(String field, String value) {
        super(String.format("School not found with %s: %s", field, value));
    }
}
