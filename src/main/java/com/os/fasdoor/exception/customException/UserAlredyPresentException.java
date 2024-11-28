package com.os.fasdoor.exception.customException;

public class UserAlredyPresentException extends RuntimeException{
    public UserAlredyPresentException(String message) {
        super(message);
    }
}
