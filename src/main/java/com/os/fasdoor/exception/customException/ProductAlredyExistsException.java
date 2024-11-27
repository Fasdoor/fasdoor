package com.os.fasdoor.exception.customException;

public class ProductAlredyExistsException extends RuntimeException{
    public ProductAlredyExistsException(String message) {
        super(message);
    }
}
