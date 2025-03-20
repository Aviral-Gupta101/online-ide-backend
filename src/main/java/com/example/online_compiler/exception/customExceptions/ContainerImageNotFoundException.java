package com.example.online_compiler.exception.customExceptions;

public class ContainerImageNotFoundException extends RuntimeException {
    public ContainerImageNotFoundException(String message) {
        super(message);
    }
}
