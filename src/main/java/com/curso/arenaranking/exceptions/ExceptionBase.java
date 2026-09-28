package com.curso.arenaranking.exceptions;

public abstract class ExceptionBase extends RuntimeException {

    protected ExceptionBase(String message) {
        super(message);
    }
}
