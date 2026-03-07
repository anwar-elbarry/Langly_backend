package com.langly.app.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final Object id;
    public ResourceNotFoundException(String resource , Object id) {
        super(resource + " not found with id: "+id);
        this.resource = resource;
        this.id = id;
    }
}
