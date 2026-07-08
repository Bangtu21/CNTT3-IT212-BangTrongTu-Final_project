package com.elearning.exceptions;

public class PrerequisiteNotCompletedException extends RuntimeException {
    public PrerequisiteNotCompletedException(String message) {
        super(message);
    }
}
