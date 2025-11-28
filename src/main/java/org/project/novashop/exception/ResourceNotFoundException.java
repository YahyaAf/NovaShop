package org.project.novashop.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " avec ID " + id + " non trouvé");
    }

    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " avec identifiant " + identifier + " non trouvé");
    }

    public ResourceNotFoundException(String resource, String field, String value) {
        super(resource + " non trouvé avec " + field + ": " + value);
    }
}