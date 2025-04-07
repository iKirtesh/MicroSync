package com.kirtesh.microsync.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String id) {
        super("Could not find address " + id);
    }
}
