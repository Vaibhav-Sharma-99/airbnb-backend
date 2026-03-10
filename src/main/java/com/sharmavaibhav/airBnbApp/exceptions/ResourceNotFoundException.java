package com.sharmavaibhav.airBnbApp.exceptions;

import org.antlr.v4.runtime.RuntimeMetaData;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
