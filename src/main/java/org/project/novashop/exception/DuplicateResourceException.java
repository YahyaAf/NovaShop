package org.project.novashop.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, String value) {
        super(resource + " avec " + field + " '" + value + "' existe déjà");
    }
}