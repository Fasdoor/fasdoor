package com.os.onestopper.exception.customException;

public class UserAlredyPresentException extends RuntimeException{
    public UserAlredyPresentException(String message) {
        super(message);
    }
}
