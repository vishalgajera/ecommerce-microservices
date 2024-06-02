package com.product.exception;

public class OutOfStockException extends RuntimeException {
    private String message;
    public OutOfStockException(String message) {
        super(message);
        this.message = message;
    }
}
