package com.edu.ulab.app.exception;

public class NoSuchEntityException extends RepositoryException {
    public NoSuchEntityException(String message) {
        super(message);
    }
}
