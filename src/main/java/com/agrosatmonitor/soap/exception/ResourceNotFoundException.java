package com.agrosatmonitor.soap.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " com ID " + id + " não encontrado(a).");
    }
}
