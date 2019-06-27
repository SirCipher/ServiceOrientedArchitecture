package com.type2labs.nevernote.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private String resource;
    private String name;
    private Object value;

    public ResourceNotFoundException(String resource, String name, Object value) {
        super(String.format("%s not found with %s : '%s'", resource, name, value));
        this.resource = resource;
        this.name = name;
        this.value = value;
    }
}
