package com.os.onestopper.exception.customException;

public class ProductAlredyExistsException extends RuntimeException{
    public ProductAlredyExistsException(String message) {
        super(message);
    }
}
