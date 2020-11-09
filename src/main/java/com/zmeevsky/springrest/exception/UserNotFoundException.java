package com.zmeevsky.springrest.exception;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String message, String path) {
        super(message, path);
    }
}
