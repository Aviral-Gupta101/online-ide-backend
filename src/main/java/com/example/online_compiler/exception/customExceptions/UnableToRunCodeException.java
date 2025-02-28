package com.example.online_compiler.exception.customExceptions;

public class UnableToRunCodeException extends RuntimeException {
  public UnableToRunCodeException(String message) {
    super(message);
  }
}
