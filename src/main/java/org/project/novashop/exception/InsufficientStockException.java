package org.project.novashop.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String product, int requested, int available) {
        super("Stock insuffisant pour " + product + ". Demandé: " + requested + ", Disponible: " + available);
    }
}